package nyumba_server.bookings.dto;

import lombok.*;
import nyumba_server.bookings.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long unitId;
    private String unitNumber;
    private String unitType;
    private String propertyName;
    private String tenantName;
    private String tenantEmail;
    private BigDecimal depositAmount;
    private BigDecimal depositPaid;
    private BigDecimal depositBalance;
    private BookingStatus status;
    private LocalDate expectedMoveInDate;
    private LocalDate actualMoveInDate;
    private String notes;
    private LocalDateTime createdAt;
}