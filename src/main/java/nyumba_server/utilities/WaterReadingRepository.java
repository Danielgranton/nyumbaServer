package nyumba_server.utilities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaterReadingRepository extends JpaRepository<WaterReading, Long> {
    List<WaterReading> findByUnitId(Long unitId);
    Optional<WaterReading> findByUnitIdAndReadingMonth(Long unitId, String readingMonth);

    List<WaterReading> findByUnitIdOrderByReadingDateDesc(Long unitId);
}