package nyumba_server.bookings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUnitIdAndStatusIn(Long unitId, List<BookingStatus> statuses);
    List<Booking> findByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.unit.property.landlord.id = :landlordId")
    List<Booking> findAllByLandlordId(Long landlordId);
}