package com.boatsafari.controller;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.model.Booking;
import com.boatsafari.service.BookingService;
import com.boatsafari.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        return new ResponseEntity<>(bookingService.createBooking(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(@RequestParam(required = false) Long userId,
                                                        HttpServletRequest request) {
        if (isCustomer(request)) {
            Long callerId = callerId(request);
            if (callerId == null || userId == null || !callerId.equals(userId)) {
                throw new AccessDeniedException("Customers can only view their own bookings.");
            }
        }
        if (userId != null) {
            return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/trip/{tripId}/seats")
    public ResponseEntity<List<String>> getTakenSeats(@PathVariable Long tripId) {
        return ResponseEntity.ok(bookingService.getTakenSeats(tripId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id, HttpServletRequest request) {
        Booking booking = bookingService.getBookingById(id);
        checkOwnership(booking, request);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String ref, HttpServletRequest request) {
        Booking booking = bookingService.getBookingByReference(ref);
        checkOwnership(booking, request);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id, @RequestParam(required = false) String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            return ResponseEntity.ok(bookingService.cancelBookingWithReason(id, reason));
        }
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<Booking> validateCustomerBooking(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(bookingService.validateCustomerBooking(id, status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    // ---- Access helpers: customers (and callers with no role header) may only see their own bookings ----

    private boolean isCustomer(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        return role == null || role.trim().isEmpty() || "CUSTOMER".equalsIgnoreCase(role.trim());
    }

    private Long callerId(HttpServletRequest request) {
        try {
            return Long.valueOf(request.getHeader("X-User-Id").trim());
        } catch (Exception e) {
            return null;
        }
    }

    private void checkOwnership(Booking booking, HttpServletRequest request) {
        if (!isCustomer(request)) {
            return;
        }
        Long callerId = callerId(request);
        if (callerId == null || booking.getUser() == null || !callerId.equals(booking.getUser().getId())) {
            throw new AccessDeniedException("You can only view your own bookings.");
        }
    }
}
