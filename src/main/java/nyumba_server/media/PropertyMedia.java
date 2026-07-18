package nyumba_server.media;

import jakarta.persistence.*;
import lombok.*;
import nyumba_server.property.Property;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    @Column(nullable = false)
    private Integer displayOrder;

    private String originalUrl;
    private String largeUrl;
    private String mediumUrl;
    private String smallUrl;
    private String thumbnailUrl;
    private String videoThumbnailUrl;

    private Integer width;
    private Integer height;
    private Long size;
    private Long duration; // seconds, for video

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}