package nyumba_server.bookings.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveInRequest {

    @NotNull
    private Long bookingId;

    @NotNull
    private BigDecimal firstRentPaid;

    @NotNull
    private LocalDate moveInDate;
}