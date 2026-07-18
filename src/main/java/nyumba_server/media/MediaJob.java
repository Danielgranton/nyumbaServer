package nyumba_server.media;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaJob {
    private String jobId;
    private String entityType;   // USER, PROPERTY, UNIT
    private Long entityId;
    private Long mediaRecordId;  // DB id to update when done
    private String mediaType;    // PROFILE_IMAGE, PROPERTY_IMAGE, PROPERTY_VIDEO etc.
    private String bucket;
    private String key;          // S3 key of the original
    private List<String> requestedSizes; // large, medium, small, thumbnail
    private String outputFormat; // webp
}