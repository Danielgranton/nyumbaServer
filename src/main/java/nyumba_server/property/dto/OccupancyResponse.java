package nyumba_server.property.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyResponse {
    private Long propertyId;
    private String propertyName;
    private String address;
    private long totalUnits;
    private long occupiedUnits;
    private long vacantUnits;
    private long bookedUnits;
    private double occupancyPercentage;
}
