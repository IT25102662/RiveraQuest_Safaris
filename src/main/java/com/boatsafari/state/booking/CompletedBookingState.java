package com.boatsafari.state.booking;

import com.boatsafari.exception.BusinessRuleException;

/**
 * Concrete State 4: COMPLETED
 * Represents a concluded trip booking. Terminal state: modifications are locked.
 */
public class CompletedBookingState implements BookingState {

    @Override
    public void confirm(BookingStateContext context) {
        throw new BusinessRuleException("Cannot re-confirm a completed booking.");
    }

    @Override
    public void validateBoarding(BookingStateContext context, String validationNotes) {
        throw new BusinessRuleException("Cannot validate a booking for a trip that has already concluded.");
    }

    @Override
    public void cancel(BookingStateContext context, String reason) {
        throw new BusinessRuleException("Cannot cancel a completed booking.");
    }

    @Override
    public void complete(BookingStateContext context) {
        throw new BusinessRuleException("Booking is already marked as completed.");
    }

    @Override
    public String getStatusName() {
        return "COMPLETED";
    }

    @Override
    public String getValidationStatusName() {
        return "VALIDATED";
    }
}
