package nyumba_server.media;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResult {
    private String originalUrl;
    private String largeUrl;
    private String mediumUrl;
    private String smallUrl;
    private String thumbnailUrl;
    private String videoThumbnailUrl;
    private Integer width;
    private Integer height;
    private Long size;
    private Long duration;
    private MediaStatus status;
}