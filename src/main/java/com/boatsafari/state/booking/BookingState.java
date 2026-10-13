package com.boatsafari.state.booking;

/**
 * State Pattern: State Interface for Booking Lifecycle.
 * Defines the contract for all state-dependent actions that can be performed
 * on a booking by the Booking / Desk Officer or Customer.
 */
public interface BookingState {

    /**
     * Confirms payment and reserves booking.
     */
    void confirm(BookingStateContext context);

    /**
     * Validates customer boarding at the counter (Desk Officer check-in).
     */
    void validateBoarding(BookingStateContext context, String validationNotes);

    /**
     * Cancels the booking and records reason.
     */
    void cancel(BookingStateContext context, String reason);

    /**
     * Marks the booking as completed once the boat trip has concluded.
     */
    void complete(BookingStateContext context);

    /**
     * Gets the standard status name (e.g. CONFIRMED, PENDING, COMPLETED, CANCELLED).
     */
    String getStatusName();

    /**
     * Gets the boarding validation status (e.g. VALIDATED, PENDING_VALIDATION).
     */
    String getValidationStatusName();
}
