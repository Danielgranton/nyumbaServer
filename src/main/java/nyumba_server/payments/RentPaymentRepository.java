package nyumba_server.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {
    List<RentPayment> findByTenantId(Long tenantId);
    List<RentPayment> findByUnitId(Long unitId);
    Optional<RentPayment> findByTenantIdAndPaymentMonth(Long tenantId, String paymentMonth);

    @Query("SELECT r FROM RentPayment r WHERE r.unit.property.landlord.id = :landlordId")
    Page<RentPayment> findAllByLandlordId(Long landlordId, Pageable pageable);

    @Query("SELECT r FROM RentPayment r WHERE r.unit.property.landlord.id = :landlordId AND r.status != 'PAID'")
    List<RentPayment> findUnpaidByLandlordId(Long landlordId);
}
