package nyumba_server.bills;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.bills.dto.MonthlyBillRequest;
import nyumba_server.bills.dto.MonthlyBillResponse;
import nyumba_server.payments.PaymentStatus;
import nyumba_server.payments.RentPaymentRepository;
import nyumba_server.tenants.Tenant;
import nyumba_server.tenants.TenantRepository;
import nyumba_server.units.Unit;
import nyumba_server.utilities.UtilityRate;
import nyumba_server.utilities.UtilityRateRepository;
import nyumba_server.utilities.WaterReading;
import nyumba_server.utilities.WaterReadingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonthlyBillService {

    private final MonthlyBillRepository monthlyBillRepository;
    private final TenantRepository tenantRepository;
    private final UtilityRateRepository utilityRateRepository;
    private final WaterReadingRepository waterReadingRepository;
    private final RentPaymentRepository rentPaymentRepository;

    public MonthlyBillResponse generateBill(MonthlyBillRequest request, User landlord) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Unit unit = tenant.getUnit();

        if (!unit.getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        // check if bill already exists for this month
        monthlyBillRepository.findByTenantIdAndBillMonth(tenant.getId(), request.getBillMonth())
                .ifPresent(b -> { throw new RuntimeException(
                        "Bill for " + request.getBillMonth() + " already exists"); });

        // get utility rates
        UtilityRate rate = utilityRateRepository.findByPropertyId(unit.getProperty().getId())
                .orElseThrow(() -> new RuntimeException("Utility rates not set for this property"));

        // get water charge for this month
        BigDecimal waterAmount = waterReadingRepository
                .findByUnitIdAndReadingMonth(unit.getId(), request.getBillMonth())
                .map(WaterReading::getAmountCharged)
                .orElse(BigDecimal.ZERO);

        // get garbage charge
        BigDecimal garbageAmount = rate.getGarbageFixedCharge();

        // rent amount
        BigDecimal rentAmount = unit.getRentAmount();

        // total
        BigDecimal totalAmount = rentAmount.add(waterAmount).add(garbageAmount);

        // calculate arrears from previous unpaid rent payments
        BigDecimal arrears = rentPaymentRepository.findByTenantId(tenant.getId())
                .stream()
                .filter(p -> p.getStatus() != PaymentStatus.PAID)
                .map(p -> p.getArrears() != null ? p.getArrears() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grandTotal = totalAmount.add(arrears);

        MonthlyBill bill = MonthlyBill.builder()
                .tenant(tenant)
                .unit(unit)
                .billMonth(request.getBillMonth())
                .rentAmount(rentAmount)
                .waterAmount(waterAmount)
                .garbageAmount(garbageAmount)
                .totalAmount(totalAmount)
                .arrears(arrears)
                .grandTotal(grandTotal)
                .status(BillStatus.PENDING)
                .dueDate(request.getDueDate())
                .build();

        monthlyBillRepository.save(bill);
        return toResponse(bill);
    }

    public List<MonthlyBillResponse> getAllBills(User landlord) {
        return monthlyBillRepository.findAllByLandlordId(landlord.getId(), org.springframework.data.domain.Pageable.unpaged()).getContent()
                .stream().map(this::toResponse).toList();
    }

    public List<MonthlyBillResponse> getBillsByMonth(String billMonth, User landlord) {
        return monthlyBillRepository.findByLandlordIdAndBillMonth(landlord.getId(), billMonth)
                .stream().map(this::toResponse).toList();
    }

    public List<MonthlyBillResponse> getUnpaidBills(User landlord) {
        return monthlyBillRepository.findUnpaidByLandlordId(landlord.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<MonthlyBillResponse> getMyBills(User user) {
        Tenant tenant = tenantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Tenant profile not found"));
        return monthlyBillRepository.findByTenantId(tenant.getId())
                .stream().map(this::toResponse).toList();
    }

    public MonthlyBillResponse markAsPaid(Long billId, User landlord) {
        MonthlyBill bill = monthlyBillRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!bill.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        bill.setStatus(BillStatus.PAID);
        bill.setPaidDate(LocalDate.now());
        monthlyBillRepository.save(bill);
        return toResponse(bill);
    }

    private MonthlyBillResponse toResponse(MonthlyBill bill) {
        return MonthlyBillResponse.builder()
                .id(bill.getId())
                .tenantId(bill.getTenant().getId())
                .tenantName(bill.getTenant().getUser().getFullName())
                .unitNumber(bill.getUnit().getUnitNumber())
                .propertyName(bill.getUnit().getProperty().getName())
                .billMonth(bill.getBillMonth())
                .rentAmount(bill.getRentAmount())
                .waterAmount(bill.getWaterAmount())
                .garbageAmount(bill.getGarbageAmount())
                .totalAmount(bill.getTotalAmount())
                .arrears(bill.getArrears())
                .grandTotal(bill.getGrandTotal())
                .status(bill.getStatus())
                .dueDate(bill.getDueDate())
                .paidDate(bill.getPaidDate())
                .createdAt(bill.getCreatedAt())
                .build();
    }
}