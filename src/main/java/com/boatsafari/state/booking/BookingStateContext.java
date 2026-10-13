package com.boatsafari.state.booking;

import com.boatsafari.model.Booking;

/**
 * State Pattern: Context Class.
 * Maintains a reference to an instance of a ConcreteState subclass that defines
 * the current state of the Booking. Delegates all lifecycle transitions to the current state.
 */
public class BookingStateContext {

    private final Booking booking;
    private BookingState currentState;

    public BookingStateContext(Booking booking) {
        this.booking = booking;
        this.currentState = BookingStateFactory.createState(booking);
    }

    public BookingStateContext(Booking booking, BookingState initialState) {
        this.booking = booking;
        this.currentState = initialState;
        syncBookingEntity();
    }

    public void setState(BookingState newState) {
        this.currentState = newState;
        syncBookingEntity();
    }

    private void syncBookingEntity() {
        if (this.booking != null && this.currentState != null) {
            this.booking.setStatus(this.currentState.getStatusName());
            this.booking.setValidationStatus(this.currentState.getValidationStatusName());
        }
    }

    public void confirm() {
        this.currentState.confirm(this);
    }

    public void validateBoarding(String validationNotes) {
        this.currentState.validateBoarding(this, validationNotes);
    }

    public void cancel(String reason) {
        this.currentState.cancel(this, reason);
    }

    public void complete() {
        this.currentState.complete(this);
    }

    public Booking getBooking() {
        return booking;
    }

    public BookingState getCurrentState() {
        return currentState;
    }
}
