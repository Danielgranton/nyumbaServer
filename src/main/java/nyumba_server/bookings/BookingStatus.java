package nyumba_server.bookings;

public enum BookingStatus {
    PENDING,      // deposit not yet paid
    CONFIRMED,    // deposit paid, unit booked
    MOVED_IN,     // tenant has moved in and paid first rent
    CANCELLED     // booking cancelled
}