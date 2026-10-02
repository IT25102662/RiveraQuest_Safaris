package com.boatsafari.service;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingRequestDTO dto);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByUser(Long userId);
    Booking getBookingById(Long id);
    Booking getBookingByReference(String reference);
    Booking cancelBooking(Long id);
    Booking cancelBookingWithReason(Long id, String reason);
    Booking updateBookingStatus(Long id, String status);
    Booking validateCustomerBooking(Long id, String validationStatus);
}
