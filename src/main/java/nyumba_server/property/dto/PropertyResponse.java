package nyumba_server.property.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyResponse {
    private Long id;
    private String name;
    private String address;
    private String type;
    private long totalUnits;
    private long occupiedUnits;
    private long vacantUnits;
    private LocalDateTime createdAt;
}
