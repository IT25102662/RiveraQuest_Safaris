package com.boatsafari.state.booking;

import com.boatsafari.exception.BusinessRuleException;

/**
 * Concrete State 3: VALIDATED / BOARDED
 * Customer's boarding pass has been verified and cleared by the Desk Officer at the counter.
 */
public class ValidatedBookingState implements BookingState {

    @Override
    public void confirm(BookingStateContext context) {
        throw new BusinessRuleException("Booking is already confirmed and validated for boarding.");
    }

    @Override
    public void validateBoarding(BookingStateContext context, String validationNotes) {
        throw new BusinessRuleException("Boarding pass has already been validated by the Desk Officer.");
    }

    @Override
    public void cancel(BookingStateContext context, String reason) {
        throw new BusinessRuleException("Cannot cancel a booking after the customer has already been validated and boarded at the desk.");
    }

    @Override
    public void complete(BookingStateContext context) {
        context.setState(new CompletedBookingState());
    }

    @Override
    public String getStatusName() {
        return "CONFIRMED";
    }

    @Override
    public String getValidationStatusName() {
        return "VALIDATED";
    }
}
