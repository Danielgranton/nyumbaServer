package nyumba_server.utilities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilityRateRepository extends JpaRepository<UtilityRate, Long> {
    Optional<UtilityRate> findByPropertyId(Long propertyId);
}