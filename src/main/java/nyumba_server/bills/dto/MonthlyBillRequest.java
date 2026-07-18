package nyumba_server.bills.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBillRequest {

    @NotNull
    private Long tenantId;

    @NotBlank
    private String billMonth; // format: "2026-07"

    @NotNull
    private LocalDate dueDate;
}