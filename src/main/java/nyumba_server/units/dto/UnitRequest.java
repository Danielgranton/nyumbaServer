package nyumba_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import nyumba_server.model.UnitType;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {

    @NotBlank
    private String unitNumber;

    @NotNull
    private UnitType unitType;

    @NotNull
    private BigDecimal rentAmount;
}