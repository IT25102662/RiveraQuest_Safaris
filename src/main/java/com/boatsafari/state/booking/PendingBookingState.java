package com.boatsafari.state.booking;

import com.boatsafari.exception.BusinessRuleException;

/**
 * Concrete State 1: PENDING
 * Represents a booking created but awaiting payment or desk officer confirmation.
 */
public class PendingBookingState implements BookingState {

    @Override
    public void confirm(BookingStateContext context) {
        context.setState(new ConfirmedBookingState());
    }

    @Override
    public void validateBoarding(BookingStateContext context, String validationNotes) {
        throw new BusinessRuleException("Cannot validate boarding for a PENDING booking. Full confirmation is required first.");
    }

    @Override
    public void cancel(BookingStateContext context, String reason) {
        context.getBooking().setCancellationReason(reason);
        context.setState(new CancelledBookingState());
    }

    @Override
    public void complete(BookingStateContext context) {
        throw new BusinessRuleException("Cannot mark a PENDING booking as completed without trip execution.");
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }

    @Override
    public String getValidationStatusName() {
        return "PENDING_VALIDATION";
    }
}
