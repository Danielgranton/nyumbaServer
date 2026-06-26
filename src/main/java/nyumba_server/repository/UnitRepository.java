package nyumba_server.repository;

import nyumba_server.model.Unit;
import nyumba_server.model.UnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    List<Unit> findByPropertyId(Long propertyId);
    List<Unit> findByPropertyIdAndStatus(Long propertyId, UnitStatus status);
    long countByPropertyIdAndStatus(Long propertyId, UnitStatus status);
}