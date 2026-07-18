package nyumba_server.utilities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaterReadingRequest {

    @NotNull
    private Long unitId;

    @NotBlank
    private String readingMonth; // format: "2026-07"

    @NotNull
    private BigDecimal previousReading;

    @NotNull
    private BigDecimal currentReading;

    @NotNull
    private LocalDate readingDate;
}