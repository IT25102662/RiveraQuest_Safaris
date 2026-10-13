package com.boatsafari.state.booking;

import com.boatsafari.model.Booking;

/**
 * Factory class for the State Pattern.
 * Restores the appropriate Concrete BookingState instance based on the entity's database fields.
 */
public class  BookingStateFactory {

    public static BookingState createState(Booking booking) {
        if (booking == null || booking.getStatus() == null) {
            return new ConfirmedBookingState();
        }

        String status = booking.getStatus().trim().toUpperCase();
        String validationStatus = booking.getValidationStatus() != null ? booking.getValidationStatus().trim().toUpperCase() : "";

        switch (status) {
            case "PENDING":
                return new PendingBookingState();
            case "CANCELLED":
                return new CancelledBookingState();
            case "COMPLETED":
                return new CompletedBookingState();
            case "CONFIRMED":
            default:
                if ("VALIDATED".equalsIgnoreCase(validationStatus)) {
                    return new ValidatedBookingState();
                }
                return new ConfirmedBookingState();
        }
    }
}
