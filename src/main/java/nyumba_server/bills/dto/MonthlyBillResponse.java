package nyumba_server.bills.dto;

import lombok.*;
import nyumba_server.bills.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyBillResponse {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private String unitNumber;
    private String propertyName;
    private String billMonth;
    private BigDecimal rentAmount;
    private BigDecimal waterAmount;
    private BigDecimal garbageAmount;
    private BigDecimal totalAmount;
    private BigDecimal arrears;
    private BigDecimal grandTotal;
    private BillStatus status;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private LocalDateTime createdAt;
}