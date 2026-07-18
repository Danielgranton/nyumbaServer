package nyumba_server.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nyumba_server.auth.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaCallbackConsumer {

    private final PropertyMediaRepository propertyMediaRepository;
    private final UnitMediaRepository unitMediaRepository;
    private final UserRepository userRepository;

    @RabbitListener(queues = "${media.callback.queue}")
    public void handleCallback(MediaCallback callback) {
        log.info("Received media callback for record: {} status: {}",
                callback.getMediaRecordId(), callback.getStatus());

        MediaStatus status = "READY".equals(callback.getStatus())
                ? MediaStatus.READY : MediaStatus.FAILED;

        switch (callback.getEntityType()) {
            case "PROPERTY" -> handlePropertyMedia(callback, status);
            case "UNIT"     -> handleUnitMedia(callback, status);
            case "USER"     -> handleUserMedia(callback);
            default -> log.warn("Unknown entity type: {}", callback.getEntityType());
        }
    }

    private void handlePropertyMedia(MediaCallback callback, MediaStatus status) {
        propertyMediaRepository.findById(callback.getMediaRecordId()).ifPresent(media -> {
            media.setStatus(status);
            media.setOriginalUrl(callback.getOriginalUrl());
            media.setLargeUrl(callback.getLargeUrl());
            media.setMediumUrl(callback.getMediumUrl());
            media.setSmallUrl(callback.getSmallUrl());
            media.setThumbnailUrl(callback.getThumbnailUrl());
            media.setVideoThumbnailUrl(callback.getVideoThumbnailUrl());
            media.setWidth(callback.getWidth());
            media.setHeight(callback.getHeight());
            media.setSize(callback.getSize());
            media.setDuration(callback.getDuration());
            propertyMediaRepository.save(media);
            log.info("Updated property media {} to {}", media.getId(), status);
        });
    }

    private void handleUnitMedia(MediaCallback callback, MediaStatus status) {
        unitMediaRepository.findById(callback.getMediaRecordId()).ifPresent(media -> {
            media.setStatus(status);
            media.setOriginalUrl(callback.getOriginalUrl());
            media.setLargeUrl(callback.getLargeUrl());
            media.setMediumUrl(callback.getMediumUrl());
            media.setSmallUrl(callback.getSmallUrl());
            media.setThumbnailUrl(callback.getThumbnailUrl());
            media.setVideoThumbnailUrl(callback.getVideoThumbnailUrl());
            media.setWidth(callback.getWidth());
            media.setHeight(callback.getHeight());
            media.setSize(callback.getSize());
            media.setDuration(callback.getDuration());
            unitMediaRepository.save(media);
            log.info("Updated unit media {} to {}", media.getId(), status);
        });
    }

    private void handleUserMedia(MediaCallback callback) {
        userRepository.findById(callback.getEntityId()).ifPresent(user -> {
            user.setProfileImageOriginal(callback.getOriginalUrl());
            user.setProfileImageLarge(callback.getLargeUrl());
            user.setProfileImageMedium(callback.getMediumUrl());
            user.setProfileImageSmall(callback.getSmallUrl());
            user.setProfileImageThumbnail(callback.getThumbnailUrl());
            userRepository.save(user);
            log.info("Updated profile image for user {}", user.getId());
        });
    }
}
