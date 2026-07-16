package nyumba_server.payments;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.payments.dto.RentPaymentRequest;
import nyumba_server.payments.dto.RentPaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class RentPaymentController {

    private final RentPaymentService rentPaymentService;

    @PostMapping
    public ResponseEntity<RentPaymentResponse> recordPayment(
            @Valid @RequestBody RentPaymentRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(rentPaymentService.recordPayment(request, landlord));
    }

    @GetMapping
    public ResponseEntity<List<RentPaymentResponse>> getAllPayments(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(rentPaymentService.getAllPayments(landlord));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentPaymentResponse>> getPaymentsForTenant(
            @PathVariable Long tenantId,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(rentPaymentService.getPaymentsForTenant(tenantId, landlord));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<RentPaymentResponse>> getUnpaid(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(rentPaymentService.getUnpaidPayments(landlord));
    }
}