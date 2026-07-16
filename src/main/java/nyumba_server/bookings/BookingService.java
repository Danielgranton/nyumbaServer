package nyumba_server.bookings;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.bookings.dto.BookingRequest;
import nyumba_server.bookings.dto.BookingResponse;
import nyumba_server.bookings.dto.MoveInRequest;
import nyumba_server.payments.PaymentStatus;
import nyumba_server.payments.RentPayment;
import nyumba_server.payments.RentPaymentRepository;
import nyumba_server.tenants.Tenant;
import nyumba_server.tenants.TenantRepository;
import nyumba_server.units.Unit;
import nyumba_server.units.UnitRepository;
import nyumba_server.units.UnitStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final TenantRepository tenantRepository;
    private final RentPaymentRepository rentPaymentRepository;

    @Transactional
    public BookingResponse bookUnit(BookingRequest request, User user) {
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (unit.getStatus() != UnitStatus.VACANT) {
            throw new RuntimeException("Unit is not available for booking");
        }

        // Check no active booking exists
        boolean alreadyBooked = bookingRepository
                .findByUnitIdAndStatusIn(unit.getId(),
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED))
                .isPresent();
        if (alreadyBooked) {
            throw new RuntimeException("Unit already has an active booking");
        }

        BigDecimal depositRequired = unit.getDepositAmount();
        BigDecimal depositPaid = request.getDepositPaid();

        BookingStatus status = depositPaid.compareTo(depositRequired) >= 0
                ? BookingStatus.CONFIRMED
                : BookingStatus.PENDING;

        Booking booking = Booking.builder()
                .unit(unit)
                .user(user)
                .depositAmount(depositRequired)
                .depositPaid(depositPaid)
                .status(status)
                .expectedMoveInDate(request.getExpectedMoveInDate())
                .notes(request.getNotes())
                .build();

        bookingRepository.save(booking);

        // Mark unit as booked
        unit.setStatus(UnitStatus.BOOKED);
        unitRepository.save(unit);

        return toResponse(booking);
    }

    @Transactional
    public BookingResponse confirmMoveIn(MoveInRequest request, User landlord) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking must be CONFIRMED before move-in");
        }

        Unit unit = booking.getUnit();

        // Create tenant profile
        Tenant tenant = Tenant.builder()
                .user(booking.getUser())
                .unit(unit)
                .moveInDate(request.getMoveInDate())
                .build();
        tenantRepository.save(tenant);

        // Record first rent payment
        BigDecimal expected = unit.getRentAmount();
        BigDecimal paid = request.getFirstRentPaid();
        BigDecimal arrears = expected.subtract(paid);
        if (arrears.compareTo(BigDecimal.ZERO) < 0) arrears = BigDecimal.ZERO;

        PaymentStatus paymentStatus;
        if (paid.compareTo(expected) >= 0) {
            paymentStatus = PaymentStatus.PAID;
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PARTIAL;
        } else {
            paymentStatus = PaymentStatus.UNPAID;
        }

        String paymentMonth = request.getMoveInDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        RentPayment firstRent = RentPayment.builder()
                .tenant(tenant)
                .unit(unit)
                .amountPaid(paid)
                .amountExpected(expected)
                .arrears(arrears)
                .status(paymentStatus)
                .paymentDate(request.getMoveInDate())
                .paymentMonth(paymentMonth)
                .notes("First rent payment on move-in")
                .build();
        rentPaymentRepository.save(firstRent);

        // Update unit to occupied
        unit.setStatus(UnitStatus.OCCUPIED);
        unitRepository.save(unit);

        // Update booking status
        booking.setStatus(BookingStatus.MOVED_IN);
        booking.setActualMoveInDate(request.getMoveInDate());
        bookingRepository.save(booking);

        return toResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, User landlord) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Free up the unit
        Unit unit = booking.getUnit();
        unit.setStatus(UnitStatus.VACANT);
        unitRepository.save(unit);

        return toResponse(booking);
    }

    public List<BookingResponse> getAllBookings(User landlord) {
        return bookingRepository.findAllByLandlordId(landlord.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponse> getMyBookings(User user) {
        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingResponse toResponse(Booking booking) {
        BigDecimal balance = booking.getDepositAmount().subtract(booking.getDepositPaid());
        if (balance.compareTo(BigDecimal.ZERO) < 0) balance = BigDecimal.ZERO;

        return BookingResponse.builder()
                .id(booking.getId())
                .unitId(booking.getUnit().getId())
                .unitNumber(booking.getUnit().getUnitNumber())
                .unitType(booking.getUnit().getUnitType().name())
                .propertyName(booking.getUnit().getProperty().getName())
                .tenantName(booking.getUser().getFullName())
                .tenantEmail(booking.getUser().getEmail())
                .depositAmount(booking.getDepositAmount())
                .depositPaid(booking.getDepositPaid())
                .depositBalance(balance)
                .status(booking.getStatus())
                .expectedMoveInDate(booking.getExpectedMoveInDate())
                .actualMoveInDate(booking.getActualMoveInDate())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}