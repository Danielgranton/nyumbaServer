package nyumba_server.bookings;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.bookings.dto.BookingRequest;
import nyumba_server.bookings.dto.BookingResponse;
import nyumba_server.bookings.dto.MoveInRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // Tenant books a unit
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<BookingResponse> bookUnit(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.bookUnit(request, user));
    }

    // Landlord confirms move-in after deposit + first rent
    @PostMapping("/move-in")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<BookingResponse> confirmMoveIn(
            @Valid @RequestBody MoveInRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(bookingService.confirmMoveIn(request, landlord));
    }

    // Landlord cancels a booking
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, landlord));
    }

    // Landlord sees all bookings
    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(bookingService.getAllBookings(landlord));
    }

    // Tenant sees their own bookings
    @GetMapping("/my")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getMyBookings(user));
    }
}