package nyumba_server.media;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaResult processProfileImage(Long userId, MultipartFile file);
    MediaResult processPropertyMedia(Long propertyId, MultipartFile file, MediaType type);
    MediaResult processUnitMedia(Long unitId, MultipartFile file, MediaType type);
    void deleteMedia(String originalUrl);
}