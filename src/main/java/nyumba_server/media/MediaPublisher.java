package nyumba_server.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${media.queue.name}")
    private String jobsQueue;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void publishImageJob(String entityType, Long entityId,
                                Long mediaRecordId, String mediaType, String s3Key) {
        MediaJob job = MediaJob.builder()
                .jobId(UUID.randomUUID().toString())
                .entityType(entityType)
                .entityId(entityId)
                .mediaRecordId(mediaRecordId)
                .mediaType(mediaType)
                .bucket(bucket)
                .key(s3Key)
                .requestedSizes(List.of("large", "medium", "small", "thumbnail"))
                .outputFormat("webp")
                .build();

        rabbitTemplate.convertAndSend(jobsQueue, job);
        log.info("Published media job: {} for entity: {} id: {}", job.getJobId(), entityType, entityId);
    }

    public void publishVideoJob(String entityType, Long entityId,
                                Long mediaRecordId, String mediaType, String s3Key) {
        MediaJob job = MediaJob.builder()
                .jobId(UUID.randomUUID().toString())
                .entityType(entityType)
                .entityId(entityId)
                .mediaRecordId(mediaRecordId)
                .mediaType(mediaType)
                .bucket(bucket)
                .key(s3Key)
                .requestedSizes(List.of("720p", "480p", "thumbnail", "gif"))
                .outputFormat("mp4")
                .build();

        rabbitTemplate.convertAndSend(jobsQueue, job);
        log.info("Published video job: {} for entity: {} id: {}", job.getJobId(), entityType, entityId);
    }
}