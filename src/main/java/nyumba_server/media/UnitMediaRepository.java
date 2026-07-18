package nyumba_server.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitMediaRepository extends JpaRepository<UnitMedia, Long> {
    List<UnitMedia> findByUnitIdOrderByDisplayOrder(Long unitId);
    long countByUnitIdAndType(Long unitId, MediaType type);
}