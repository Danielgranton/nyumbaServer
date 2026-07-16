package nyumba_server.property;

import nyumba_server.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByLandlordId(Long landlordId);
}