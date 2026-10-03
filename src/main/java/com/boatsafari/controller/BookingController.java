package com.boatsafari.controller;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.model.Booking;
import com.boatsafari.service.BookingService;
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
    public ResponseEntity<List<Booking>> getAllBookings(@RequestParam(required = false) Long userId) {
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
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/reference/{ref}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String ref) {
        return ResponseEntity.ok(bookingService.getBookingByReference(ref));
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
}
