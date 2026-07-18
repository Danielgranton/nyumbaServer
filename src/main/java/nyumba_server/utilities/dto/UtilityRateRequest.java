package nyumba_server.utilities.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtilityRateRequest {

    @NotNull
    private Long propertyId;

    @NotNull
    private BigDecimal waterRatePerUnit;

    @NotNull
    private BigDecimal garbageFixedCharge;
}