package nyumba_server.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {
    List<RentPayment> findByTenantId(Long tenantId);
    List<RentPayment> findByUnitId(Long unitId);
    List<RentPayment> findByTenantIdAndPaymentMonth(Long tenantId, String paymentMonth);
    Optional<RentPayment> findByTenantIdAndPaymentMonthAndUnit_Property_Landlord_Id(
            Long tenantId, String paymentMonth, Long landlordId);

    @Query("SELECT r FROM RentPayment r WHERE r.unit.property.landlord.id = :landlordId")
    List<RentPayment> findAllByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT r FROM RentPayment r WHERE r.unit.property.landlord.id = :landlordId AND r.status != 'PAID'")
    List<RentPayment> findUnpaidByLandlordId(@Param("landlordId") Long landlordId);
}