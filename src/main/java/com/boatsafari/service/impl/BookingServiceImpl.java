package com.boatsafari.service.impl;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingOfficer;
import com.boatsafari.model.Customer;
import com.boatsafari.model.Promotion;
import com.boatsafari.model.Trip;
import com.boatsafari.repository.BookingOfficerRepository;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.CustomerRepository;
import com.boatsafari.repository.PromotionRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.BookingService;
import com.boatsafari.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private BookingOfficerRepository bookingOfficerRepository;

    @Autowired
    private PromotionService promotionService;

    @Override
    public synchronized Booking createBooking(BookingRequestDTO dto) {
        Customer customer;
        if (dto.getUserId() != null) {
            customer = customerRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + dto.getUserId()));
        } else if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().trim().isEmpty()) {
            customer = customerRepository.findByEmail(dto.getCustomerEmail().trim())
                    .orElseGet(() -> {
                        String rawName = dto.getCustomerName() != null ? dto.getCustomerName().trim() : "Walk-in Customer";
                        String[] parts = rawName.split(" ", 2);
                        Customer c = new Customer();
                        c.setFirstName(parts[0]);
                        c.setLastName(parts.length > 1 ? parts[1] : "");
                        c.setEmail(dto.getCustomerEmail().trim());
                        c.setPasswordHash("walkin123");
                        c.setPhoneNumber(dto.getCustomerPhone());
                        c.setNicOrPassport(dto.getCustomerNic());
                        return customerRepository.save(c);
                    });
        } else {
            throw new BusinessRuleException("Either a userId or customer name/email is required to create a booking.");
        }

        Trip trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + dto.getTripId()));

        if ("Cancelled".equalsIgnoreCase(trip.getStatus()) || "Completed".equalsIgnoreCase(trip.getStatus())) {
            throw new BusinessRuleException("Cannot book a trip that is " + trip.getStatus());
        }

        LocalDateTime departure = LocalDateTime.of(trip.getTripDate(), trip.getDepartureTime());
        if (departure.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("This departure (" + trip.getTripDate() + " " + trip.getDepartureTime()
                    + ") has already left. Please choose an upcoming trip.");
        }

        if (dto.getSeatCount() > trip.getAvailableSeats()) {
            throw new BusinessRuleException("Overbooking Error: Requested " + dto.getSeatCount() +
                    " seat(s), but only " + trip.getAvailableSeats() + " seat(s) remain available!");
        }

        // Seat validation (runs before any voucher or seat counters are changed)
        List<String> takenSeats = getTakenSeats(trip.getId());
        String seats = dto.getSeatNumbers();
        if (seats == null || seats.trim().isEmpty()) {
            List<String> assigned = new ArrayList<>();
            for (int i = 1; i <= trip.getPassengerCapacity() && assigned.size() < dto.getSeatCount(); i++) {
                String label = String.format("S-%02d", i);
                if (!takenSeats.contains(label)) assigned.add(label);
            }
            if (assigned.size() < dto.getSeatCount()) {
                throw new BusinessRuleException("Not enough free seats are left on this trip.");
            }
            seats = String.join(", ", assigned);
        } else {
            List<String> requested = new ArrayList<>();
            for (String raw : seats.split(",")) {
                String seat = raw.trim().toUpperCase();
                if (seat.isEmpty()) continue;
                if (!seat.matches("S-\\d{1,3}")) {
                    throw new BusinessRuleException("Invalid seat number: " + raw.trim());
                }
                if (requested.contains(seat)) {
                    throw new BusinessRuleException("Seat " + seat + " was selected more than once.");
                }
                if (takenSeats.contains(seat)) {
                    throw new BusinessRuleException("Seat " + seat + " is already booked. Please choose another seat.");
                }
                requested.add(seat);
            }
            if (requested.size() != dto.getSeatCount()) {
                throw new BusinessRuleException("The number of seat numbers (" + requested.size()
                        + ") does not match the seat count (" + dto.getSeatCount() + ").");
            }
            seats = String.join(", ", requested);
        }

        double totalPrice = trip.getPrice() * dto.getSeatCount();
        double discountAmount = 0.0;
        Promotion appliedPromo = null;

        if (dto.getPromoCode() != null && !dto.getPromoCode().trim().isEmpty()) {
            appliedPromo = promotionRepository.findByCodeIgnoreCase(dto.getPromoCode().trim())
                    .orElseThrow(() -> new BusinessRuleException("Invalid voucher / discount code: " + dto.getPromoCode()));

            discountAmount = promotionService.calculateDiscount(appliedPromo, trip, totalPrice);
            appliedPromo.setUsageCount(appliedPromo.getUsageCount() + 1);
            promotionRepository.save(appliedPromo);
        }

        double finalPrice = totalPrice - discountAmount;

        trip.setBookedSeats(trip.getBookedSeats() + dto.getSeatCount());
        tripRepository.save(trip);

        String reference = "BK-2026-" + (1000 + (long) (Math.random() * 9000));
        Booking booking = new Booking();
        booking.setBookingReference(reference);
        booking.setUser(customer);
        booking.setTrip(trip);
        booking.setSeatCount(dto.getSeatCount());
        booking.setSeatNumbers(seats);
        booking.setTotalPrice(totalPrice);
        booking.setDiscountAmount(discountAmount);
        booking.setFinalPrice(finalPrice);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setPaymentMethod(dto.getPaymentMethod());
        booking.setNotes(dto.getNotes());
        booking.setPromotion(appliedPromo);

        if (dto.getBookingOfficerId() != null) {
            BookingOfficer officer = bookingOfficerRepository.findById(dto.getBookingOfficerId()).orElse(null);
            booking.setBookingOfficer(officer);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public List<String> getTakenSeats(Long tripId) {
        List<String> taken = new ArrayList<>();
        for (Booking b : bookingRepository.findByTripIdAndStatusNot(tripId, "CANCELLED")) {
            if (b.getSeatNumbers() == null) continue;
            for (String s : b.getSeatNumbers().split(",")) {
                String seat = s.trim().toUpperCase();
                if (!seat.isEmpty() && !taken.contains(seat)) taken.add(seat);
            }
        }
        return taken;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
    }

    @Override
    public Booking getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + reference));
    }

    @Override
    public Booking cancelBooking(Long id) {
        return cancelBookingWithReason(id, "Cancelled by user/desk officer");
    }

    @Override
    public Booking cancelBookingWithReason(Long id, String reason) {
        Booking booking = getBookingById(id);
        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new BusinessRuleException("Booking is already cancelled!");
        }

        booking.setStatus("CANCELLED");
        booking.setCancellationReason(reason);

        Trip trip = booking.getTrip();
        trip.setBookedSeats(Math.max(0, trip.getBookedSeats() - booking.getSeatCount()));
        tripRepository.save(trip);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = getBookingById(id);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking validateCustomerBooking(Long id, String validationStatus) {
        Booking booking = getBookingById(id);
        booking.setValidationStatus(validationStatus);
        return bookingRepository.save(booking);
    }
}
