package nyumba_server.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByTenantId(Long tenantId);

    List<MaintenanceRequest> findByUnitId(Long unitId);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.unit.property.landlord.id = :landlordId")
    List<MaintenanceRequest> findAllByLandlordId(Long landlordId);

    @Query("SELECT m FROM MaintenanceRequest m WHERE m.unit.property.landlord.id = :landlordId AND m.status = :status")
    List<MaintenanceRequest> findByLandlordIdAndStatus(Long landlordId, MaintenanceStatus status);
}