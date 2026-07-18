package nyumba_server.utilities.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterReadingResponse {
    private Long id;
    private Long unitId;
    private String unitNumber;
    private String propertyName;
    private String readingMonth;
    private BigDecimal previousReading;
    private BigDecimal currentReading;
    private BigDecimal unitsConsumed;
    private BigDecimal amountCharged;
    private LocalDate readingDate;
    private LocalDateTime createdAt;
}