package nyumba_server.bills;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MonthlyBillRepository extends JpaRepository<MonthlyBill, Long> {

    Optional<MonthlyBill> findByTenantIdAndBillMonth(Long tenantId, String billMonth);

    List<MonthlyBill> findByTenantId(Long tenantId);

    @Query("SELECT b FROM MonthlyBill b WHERE b.unit.property.landlord.id = :landlordId")
    List<MonthlyBill> findAllByLandlordId(Long landlordId);

    @Query("SELECT b FROM MonthlyBill b WHERE b.unit.property.landlord.id = :landlordId AND b.billMonth = :billMonth")
    List<MonthlyBill> findByLandlordIdAndBillMonth(Long landlordId, String billMonth);

    @Query("SELECT b FROM MonthlyBill b WHERE b.unit.property.landlord.id = :landlordId AND b.status != 'PAID'")
    List<MonthlyBill> findUnpaidByLandlordId(Long landlordId);
}