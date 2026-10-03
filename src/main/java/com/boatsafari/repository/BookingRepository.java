package com.boatsafari.repository;

import com.boatsafari.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTripId(Long tripId);
    List<Booking> findByTripIdAndStatusNot(Long tripId, String status);
    List<Booking> findByStatus(String status);
}