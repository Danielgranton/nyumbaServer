package nyumba_server.dto;

import lombok.*;
import nyumba_server.model.UnitStatus;
import nyumba_server.model.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitResponse {
    private Long id;
    private String unitNumber;
    private UnitType unitType;
    private BigDecimal rentAmount;
    private UnitStatus status;
    private Long propertyId;
    private String propertyName;
    private LocalDateTime createdAt;
}