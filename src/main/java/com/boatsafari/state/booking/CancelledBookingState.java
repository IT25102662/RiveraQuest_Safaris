package com.boatsafari.state.booking;

import com.boatsafari.exception.BusinessRuleException;

/**
 * Concrete State 5: CANCELLED
 * Represents a cancelled booking where seats and vouchers have been released.
 * Terminal state.
 */
public class CancelledBookingState implements BookingState {

    @Override
    public void confirm(BookingStateContext context) {
        throw new BusinessRuleException("Cannot re-confirm an already cancelled booking.");
    }

    @Override
    public void validateBoarding(BookingStateContext context, String validationNotes) {
        throw new BusinessRuleException("Cannot validate boarding for a cancelled booking!");
    }

    @Override
    public void cancel(BookingStateContext context, String reason) {
        throw new BusinessRuleException("Booking is already cancelled.");
    }

    @Override
    public void complete(BookingStateContext context) {
        throw new BusinessRuleException("Cannot complete a cancelled booking.");
    }

    @Override
    public String getStatusName() {
        return "CANCELLED";
    }

    @Override
    public String getValidationStatusName() {
        return "PENDING_VALIDATION";
    }
}
