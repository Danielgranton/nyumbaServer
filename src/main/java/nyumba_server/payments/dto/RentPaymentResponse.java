package nyumba_server.payments.dto;

import lombok.*;
import nyumba_server.payments.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentPaymentResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String unitNumber;
    private String propertyName;
    private BigDecimal amountPaid;
    private BigDecimal amountExpected;
    private BigDecimal arrears;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private String paymentMonth;
    private String notes;
    private LocalDateTime createdAt;
}