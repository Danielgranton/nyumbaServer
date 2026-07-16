package nyumba_server.payments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentPaymentRequest {

    @NotNull
    private Long tenantId;

    @NotNull
    private BigDecimal amountPaid;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private String paymentMonth; // format: "2026-07"

    private String notes;
}