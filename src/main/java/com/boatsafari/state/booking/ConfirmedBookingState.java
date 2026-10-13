package com.boatsafari.state.booking;

import com.boatsafari.exception.BusinessRuleException;

/**
 * Concrete State 2: CONFIRMED
 * Represents a successfully booked and paid trip.
 * Ready for Desk Officer boarding validation or cancellation.
 */
public class ConfirmedBookingState implements BookingState {

    @Override
    public void confirm(BookingStateContext context) {
        throw new BusinessRuleException("Booking is already in CONFIRMED state.");
    }

    @Override
    public void validateBoarding(BookingStateContext context, String validationNotes) {
        if (validationNotes != null && !validationNotes.trim().isEmpty()) {
            String existingNotes = context.getBooking().getNotes();
            context.getBooking().setNotes(existingNotes != null ? existingNotes + " | " + validationNotes : validationNotes);
        }
        context.setState(new ValidatedBookingState());
    }

    @Override
    public void cancel(BookingStateContext context, String reason) {
        context.getBooking().setCancellationReason(reason);
        context.setState(new CancelledBookingState());
    }

    @Override
    public void complete(BookingStateContext context) {
        // Direct transition if validation was bypassed by desk operations
        context.setState(new CompletedBookingState());
    }

    @Override
    public String getStatusName() {
        return "CONFIRMED";
    }

    @Override
    public String getValidationStatusName() {
        return "PENDING_VALIDATION";
    }
}
