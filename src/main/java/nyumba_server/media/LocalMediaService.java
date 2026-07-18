package nyumba_server.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalMediaService implements MediaService {

    private final S3Service s3Service;
    private final MediaPublisher mediaPublisher;

    @Override
    public MediaResult processProfileImage(Long userId, MultipartFile file) {
        validateImage(file);
        String key = s3Service.uploadOriginal(file, "profiles/" + userId);
        mediaPublisher.publishImageJob("USER", userId, null, "PROFILE_IMAGE", key);
        return MediaResult.builder()
                .originalUrl(s3Service.getPublicUrl(key))
                .status(MediaStatus.PROCESSING)
                .size(file.getSize())
                .build();
    }

    @Override
    public MediaResult processPropertyMedia(Long propertyId, MultipartFile file, MediaType type) {
        if (type == MediaType.IMAGE) validateImage(file);
        else validateVideo(file);
        String key = s3Service.uploadOriginal(file, "properties/" + propertyId);
        return MediaResult.builder()
                .originalUrl(s3Service.getPublicUrl(key))
                .status(MediaStatus.PROCESSING)
                .size(file.getSize())
                .build();
    }

    @Override
    public MediaResult processUnitMedia(Long unitId, MultipartFile file, MediaType type) {
        if (type == MediaType.IMAGE) validateImage(file);
        else validateVideo(file);
        String key = s3Service.uploadOriginal(file, "units/" + unitId);
        return MediaResult.builder()
                .originalUrl(s3Service.getPublicUrl(key))
                .status(MediaStatus.PROCESSING)
                .size(file.getSize())
                .build();
    }

    @Override
    public void deleteMedia(String originalUrl) {
        s3Service.deleteObject(originalUrl);
    }

    private void validateImage(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) throw new RuntimeException("Invalid file");
        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        if (!ext.matches("jpg|jpeg|png|webp"))
            throw new RuntimeException("Invalid image format. Allowed: jpg, jpeg, png, webp");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new RuntimeException("Image too large. Maximum 10MB");
    }

    private void validateVideo(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) throw new RuntimeException("Invalid file");
        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        if (!ext.matches("mp4|mov|mkv"))
            throw new RuntimeException("Invalid video format. Allowed: mp4, mov, mkv");
        if (file.getSize() > 500 * 1024 * 1024)
            throw new RuntimeException("Video too large. Maximum 500MB");
    }
}
