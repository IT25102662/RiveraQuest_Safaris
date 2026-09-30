package com.boatsafari.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingRequestDTO {

    private Long userId;

    private Long tripId;

    @NotNull(message = "Seat count is required")
    @Min(value = 1, message = "Minimum 1 seat required")
    private Integer seatCount;

    private String seatNumbers;

    private String promoCode;

    private String paymentMethod = "ONLINE_CARD";

    private String notes;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerNic;

    // Report-required field, added alongside your existing fields — set when a Desk Officer creates the booking
    private Long bookingOfficerId;

    public BookingRequestDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public Integer getSeatCount() { return seatCount; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }

    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerNic() { return customerNic; }
    public void setCustomerNic(String customerNic) { this.customerNic = customerNic; }

    public Long getBookingOfficerId() { return bookingOfficerId; }
    public void setBookingOfficerId(Long bookingOfficerId) { this.bookingOfficerId = bookingOfficerId; }
}