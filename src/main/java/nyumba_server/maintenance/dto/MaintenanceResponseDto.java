package nyumba_server.maintenance.dto;

import lombok.*;
import nyumba_server.maintenance.MaintenanceStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponseDto {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private Long unitId;
    private String unitNumber;
    private String propertyName;
    private String title;
    private String description;
    private String imageUrl;
    private MaintenanceStatus status;
    private String landlordNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}