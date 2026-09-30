package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private Integer seatCount;

    private String seatNumbers;

    @Column(nullable = false)
    private Double totalPrice;

    private Double discountAmount = 0.0;

    @Column(nullable = false)
    private Double finalPrice;

    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();

    @Column(nullable = false)
    private String status = "CONFIRMED";

    private String validationStatus = "VALIDATED";

    private String cancellationReason;

    private String paymentMethod = "ONLINE_CARD";

    private String notes;

    // Report-required fields, added alongside your existing fields
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_officer_id")
    private BookingOfficer bookingOfficer;

    public Booking() {}

    public Booking(Long id, String bookingReference, Customer user, Trip trip, Integer seatCount, String seatNumbers, Double totalPrice, Double discountAmount, Double finalPrice, LocalDateTime bookingDate, String status, String paymentMethod, String notes) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.user = user;
        this.trip = trip;
        this.seatCount = seatCount;
        this.seatNumbers = seatNumbers;
        this.totalPrice = totalPrice;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.bookingDate = bookingDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Customer getUser() { return user; }
    public void setUser(Customer user) { this.user = user; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }

    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Promotion getPromotion() { return promotion; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }

    public BookingOfficer getBookingOfficer() { return bookingOfficer; }
    public void setBookingOfficer(BookingOfficer bookingOfficer) { this.bookingOfficer = bookingOfficer; }
}