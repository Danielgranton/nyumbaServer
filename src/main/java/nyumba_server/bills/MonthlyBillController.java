package nyumba_server.bills;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.bills.dto.MonthlyBillRequest;
import nyumba_server.bills.dto.MonthlyBillResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class MonthlyBillController {

    private final MonthlyBillService monthlyBillService;

    // Landlord generates a bill for a tenant
    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MonthlyBillResponse> generate(
            @Valid @RequestBody MonthlyBillRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(monthlyBillService.generateBill(request, landlord));
    }

    // Landlord sees all bills
    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MonthlyBillResponse>> allBills(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(monthlyBillService.getAllBills(landlord));
    }

    // Landlord sees bills for a specific month
    @GetMapping("/month/{billMonth}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MonthlyBillResponse>> billsByMonth(
            @PathVariable String billMonth,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(monthlyBillService.getBillsByMonth(billMonth, landlord));
    }

    // Landlord sees all unpaid bills
    @GetMapping("/unpaid")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MonthlyBillResponse>> unpaidBills(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(monthlyBillService.getUnpaidBills(landlord));
    }

    // Landlord marks bill as paid
    @PatchMapping("/{id}/paid")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MonthlyBillResponse> markPaid(
            @PathVariable Long id,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(monthlyBillService.markAsPaid(id, landlord));
    }

    // Tenant views their own bills
    @GetMapping("/my")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<MonthlyBillResponse>> myBills(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(monthlyBillService.getMyBills(user));
    }
}