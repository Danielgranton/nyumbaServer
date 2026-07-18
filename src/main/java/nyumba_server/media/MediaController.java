package nyumba_server.media;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.auth.UserRepository;
import nyumba_server.property.Property;
import nyumba_server.property.PropertyRepository;
import nyumba_server.units.Unit;
import nyumba_server.units.UnitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final MediaPublisher mediaPublisher;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final PropertyMediaRepository propertyMediaRepository;
    private final UnitMediaRepository unitMediaRepository;

    // ── Profile image ──────────────────────────────────────────

    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {

        MediaResult result = mediaService.processProfileImage(currentUser.getId(), file);

        currentUser.setProfileImageOriginal(result.getOriginalUrl());
        currentUser.setProfileImageLarge(result.getLargeUrl());
        currentUser.setProfileImageMedium(result.getMediumUrl());
        currentUser.setProfileImageSmall(result.getSmallUrl());
        currentUser.setProfileImageThumbnail(result.getThumbnailUrl());
        userRepository.save(currentUser);

        return ResponseEntity.ok(result);
    }

    // ── Property media ──────────────────────────────────────────

    @PostMapping("/property/{propertyId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> uploadPropertyMedia(
            @PathVariable Long propertyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MediaType type,
            @AuthenticationPrincipal User landlord) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        long imageCount = propertyMediaRepository.countByPropertyIdAndType(propertyId, MediaType.IMAGE);
        long videoCount = propertyMediaRepository.countByPropertyIdAndType(propertyId, MediaType.VIDEO);

        if (type == MediaType.IMAGE && imageCount >= 5)
            throw new RuntimeException("Maximum 5 images per property");
        if (type == MediaType.VIDEO && videoCount >= 1)
            throw new RuntimeException("Maximum 1 video per property");

        MediaResult result = mediaService.processPropertyMedia(propertyId, file, type);

        PropertyMedia media = PropertyMedia.builder()
                .property(property)
                .type(type)
                .displayOrder((int) (type == MediaType.IMAGE ? imageCount : videoCount))
                .originalUrl(result.getOriginalUrl())
                .size(result.getSize())
                .status(MediaStatus.PROCESSING)
                .build();

        propertyMediaRepository.save(media);

        // publish job to queue — C++ picks this up, processes, sends callback
        if (type == MediaType.IMAGE) {
            mediaPublisher.publishImageJob(
                    "PROPERTY", propertyId, media.getId(), "PROPERTY_IMAGE", result.getOriginalUrl());
        } else {
            mediaPublisher.publishVideoJob(
                    "PROPERTY", propertyId, media.getId(), "PROPERTY_VIDEO", result.getOriginalUrl());
        }

        return ResponseEntity.ok(media);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<PropertyMedia>> getPropertyMedia(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(
                propertyMediaRepository.findByPropertyIdOrderByDisplayOrder(propertyId));
    }

    @DeleteMapping("/property/media/{mediaId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deletePropertyMedia(
            @PathVariable Long mediaId,
            @AuthenticationPrincipal User landlord) {

        PropertyMedia media = propertyMediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (!media.getProperty().getLandlord().getId().equals(landlord.getId()))
            throw new RuntimeException("Access denied");

        mediaService.deleteMedia(media.getOriginalUrl());
        media.setStatus(MediaStatus.DELETED);
        propertyMediaRepository.save(media);
        return ResponseEntity.noContent().build();
    }

    // ── Unit media ──────────────────────────────────────────────

    @PostMapping("/unit/{unitId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<?> uploadUnitMedia(
            @PathVariable Long unitId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MediaType type,
            @AuthenticationPrincipal User landlord) {

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getLandlord().getId().equals(landlord.getId()))
            throw new RuntimeException("Access denied");

        long imageCount = unitMediaRepository.countByUnitIdAndType(unitId, MediaType.IMAGE);
        long videoCount = unitMediaRepository.countByUnitIdAndType(unitId, MediaType.VIDEO);

        if (type == MediaType.IMAGE && imageCount >= 5)
            throw new RuntimeException("Maximum 5 images per unit");
        if (type == MediaType.VIDEO && videoCount >= 1)
            throw new RuntimeException("Maximum 1 video per unit");

        MediaResult result = mediaService.processUnitMedia(unitId, file, type);

        UnitMedia media = UnitMedia.builder()
                .unit(unit)
                .type(type)
                .displayOrder((int) (type == MediaType.IMAGE ? imageCount : videoCount))
                .originalUrl(result.getOriginalUrl())
                .size(result.getSize())
                .status(MediaStatus.PROCESSING)
                .build();

        unitMediaRepository.save(media);

        // publish job to queue
        if (type == MediaType.IMAGE) {
            mediaPublisher.publishImageJob(
                    "UNIT", unitId, media.getId(), "UNIT_IMAGE", result.getOriginalUrl());
        } else {
            mediaPublisher.publishVideoJob(
                    "UNIT", unitId, media.getId(), "UNIT_VIDEO", result.getOriginalUrl());
        }

        return ResponseEntity.ok(media);
    }

    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<UnitMedia>> getUnitMedia(
            @PathVariable Long unitId) {
        return ResponseEntity.ok(
                unitMediaRepository.findByUnitIdOrderByDisplayOrder(unitId));
    }

    @GetMapping("/property/{propertyId}/status")
    public ResponseEntity<?> getPropertyMediaStatus(@PathVariable Long propertyId) {
        List<PropertyMedia> media = propertyMediaRepository.findByPropertyIdOrderByDisplayOrder(propertyId);
        long ready = media.stream().filter(m -> m.getStatus() == MediaStatus.READY).count();
        long processing = media.stream().filter(m -> m.getStatus() == MediaStatus.PROCESSING).count();
        long failed = media.stream().filter(m -> m.getStatus() == MediaStatus.FAILED).count();
        return ResponseEntity.ok(java.util.Map.of(
            "total", media.size(), "ready", ready, "processing", processing, "failed", failed));
    }

    @GetMapping("/unit/{unitId}/status")
    public ResponseEntity<?> getUnitMediaStatus(@PathVariable Long unitId) {
        List<UnitMedia> media = unitMediaRepository.findByUnitIdOrderByDisplayOrder(unitId);
        long ready = media.stream().filter(m -> m.getStatus() == MediaStatus.READY).count();
        long processing = media.stream().filter(m -> m.getStatus() == MediaStatus.PROCESSING).count();
        long failed = media.stream().filter(m -> m.getStatus() == MediaStatus.FAILED).count();
        return ResponseEntity.ok(java.util.Map.of(
            "total", media.size(), "ready", ready, "processing", processing, "failed", failed));
    }

    @DeleteMapping("/unit/media/{mediaId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteUnitMedia(
            @PathVariable Long mediaId,
            @AuthenticationPrincipal User landlord) {

        UnitMedia media = unitMediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (!media.getUnit().getProperty().getLandlord().getId().equals(landlord.getId()))
            throw new RuntimeException("Access denied");

        mediaService.deleteMedia(media.getOriginalUrl());
        media.setStatus(MediaStatus.DELETED);
        unitMediaRepository.save(media);
        return ResponseEntity.noContent().build();
    }
}
