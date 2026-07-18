package nyumba_server.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyMediaRepository extends JpaRepository<PropertyMedia, Long> {
    List<PropertyMedia> findByPropertyIdOrderByDisplayOrder(Long propertyId);
    long countByPropertyIdAndType(Long propertyId, MediaType type);
}