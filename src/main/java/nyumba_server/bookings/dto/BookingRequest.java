package nyumba_server.bookings.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull
    private Long unitId;

    @NotNull
    private BigDecimal depositPaid;

    @NotNull
    private LocalDate expectedMoveInDate;

    private String notes;
}