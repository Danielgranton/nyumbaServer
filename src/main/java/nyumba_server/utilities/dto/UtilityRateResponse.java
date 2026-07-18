package nyumba_server.utilities.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilityRateResponse {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private BigDecimal waterRatePerUnit;
    private BigDecimal garbageFixedCharge;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}