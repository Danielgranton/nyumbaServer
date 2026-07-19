package nyumba_server.payments;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.payments.dto.RentPaymentRequest;
import nyumba_server.payments.dto.RentPaymentResponse;
import nyumba_server.tenants.Tenant;
import nyumba_server.tenants.TenantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentPaymentService {

    private final RentPaymentRepository rentPaymentRepository;
    private final TenantRepository tenantRepository;

    public RentPaymentResponse recordPayment(RentPaymentRequest request, User landlord) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        if (!tenant.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        BigDecimal expected = tenant.getUnit().getRentAmount();
        BigDecimal paid = request.getAmountPaid();
        BigDecimal arrears = expected.subtract(paid);

        PaymentStatus status;
        if (paid.compareTo(expected) >= 0) {
            status = PaymentStatus.PAID;
            arrears = BigDecimal.ZERO;
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            status = PaymentStatus.PARTIAL;
        } else {
            status = PaymentStatus.UNPAID;
        }

        RentPayment payment = RentPayment.builder()
                .tenant(tenant)
                .unit(tenant.getUnit())
                .amountPaid(paid)
                .amountExpected(expected)
                .arrears(arrears)
                .status(status)
                .paymentDate(request.getPaymentDate())
                .paymentMonth(request.getPaymentMonth())
                .notes(request.getNotes())
                .build();

        rentPaymentRepository.save(payment);
        return toResponse(payment);
    }

    public List<RentPaymentResponse> getAllPayments(User landlord) {
        return rentPaymentRepository.findAllByLandlordId(landlord.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent()
                .stream().map(this::toResponse).toList();
    }

    public List<RentPaymentResponse> getPaymentsForTenant(Long tenantId, User landlord) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        if (!tenant.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }
        return rentPaymentRepository.findByTenantId(tenantId)
                .stream().map(this::toResponse).toList();
    }

    public List<RentPaymentResponse> getUnpaidPayments(User landlord) {
        return rentPaymentRepository.findUnpaidByLandlordId(landlord.getId())
                .stream().map(this::toResponse).toList();
    }

    private RentPaymentResponse toResponse(RentPayment payment) {
        return RentPaymentResponse.builder()
                .id(payment.getId())
                .tenantId(payment.getTenant().getId())
                .tenantName(payment.getTenant().getUser().getFullName())
                .unitNumber(payment.getUnit().getUnitNumber())
                .propertyName(payment.getUnit().getProperty().getName())
                .amountPaid(payment.getAmountPaid())
                .amountExpected(payment.getAmountExpected())
                .arrears(payment.getArrears())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .paymentMonth(payment.getPaymentMonth())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}