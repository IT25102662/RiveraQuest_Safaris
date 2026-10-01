package com.boatsafari.service.impl;

import com.boatsafari.dto.BookingRequestDTO;
import com.boatsafari.exception. BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com. boatsafari.model. Booking;
import com. boatsafari.model. BookingOfficer;
import com.boatsafari.model.Customer;
import com.boatsafari.model. Promotion;
import com.boatsafari.model.Trip;
import com. boatsafari.repository.BookingOfficerRepository;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.CustomerRepository;
import com. boatsafari.repository.PromotionRepository;
import com. boatsafari.repository.TripRepository;
import com. boatsafari.service.Bookingservice;
import org. springframework.beans.factory.annotation.Autowired;
import org. springframework.stereotype.service;

import java. time. LocalDateTime;
import java. util. List;

aservice
public class BookingserviceImpl implements Bookingservice {

#Autowired
private BookingRepository bookingRepository;

#Autowired
private CustomerRepository oustomerRepository;

#Autowired
private TripRepository tripRepository;

#Autowired
private PromotionRepository promotionRepository;

#Autowired
private BookingOfficerRepository bookingOfficerRepository;

soverride
public synchronized Booking createBooking (BookingequestDTO dto) {
Customer customer;
if (dto.getUserid() != null} {
customer = customerRepository.findById(dto.getUserId())
.orElseThrow(() .> new ResourceNotFoundException("Customer not found with ID: " + dto.getUserid())} ;
} else if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().trim() .isEmpty(}> {
customer = customerRepository.findByEmail(dto.getCustomerEmail(} .trim()]

String rawName = dto.getCustomerName() != null ? dto.getCustomerName () .trim() : "Walk-in Customer";
string [] parts = rawName. split(" ", 2) ;
Customer c = new Customer() ;
c.setFirstName (parts [0]) ;
c.setLastName(parts.length > 1 7 parts [1] : "");
c.setEmail(dto.getCustomerEmail () .trim()} ;
c.setPasswordHash ("walkin123");
c.setPhoneNumber (dto. getCustomerPhone () ) ;
c.setNicOrPassport (dto.getCustomerNic () ) ;
return oustomerRepository.save(c) ;

} else {
throw new BusinessRuleException("Either a userId or customer name/email is required to create a booking.");
}
Trip trip = tripRepository.findById(dto.getTripId(])
.orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + dto.getTripId()] );

if ("Cancelled".equalsIgnoreCase(trip.getstatus(] ) | | "Completed" . equalsIgnoreCase (trip.getstatus()) ) {
throw new BusinessRuleException("Cannot book a trip that is " + trip. getstatus());

if (dto.getseatCount() > trip.getAvailableBeats()) {
throw new BusinessRuleException("Overbooking Error: Requested " + dto.getseatCount (] +
" seat(s), but only " + trip.getAvailableSeats() + " seat(s) remain available!") ;

double totalPrice = trip.getPrice() . dto.getseatCount ();
double discountAmount = 0.0;
Promotion appliedPromo = null;

if (dto.getPromoCode() != null && !dto.getPromoCode() .trim() .isEmpty()) {
appliedPromo = promotionRepository.findByCodeIgnoreCase (dto.getPromoCode() .trim() ]
.orElseThrow(() -> new BusinessRuleException("Invalid voucher / discount code: " + dto.getPromoCode () ) ) ;

if (!Boolean.TRUE. equals (appliedPromo.getAotive())) {
throw new BusinessRuleException( "Voucher code '" + dto. getPromoCode() + "' is no longer active!");

discountAmount = (totalPrice * appliedPromo.getDiscountPercentage()) / 100.0;
appliedPromo.setUsageCount (appliedPromo.getUsageCount() + 1);
promotionRepository. save (appliedPromo] ;

1

double finalPrice = totalPrice . discountAmount;

String seats = dto.getSeatNumbers () ;
if (seats == null || seats.trim().isEmpty()) {
int startseat = trip.getBookedseats() + 1;
StringBuilder sb = new StringBuilder () ;
for (int i = 0; i < dto.getseatCount(); i++) {
if (i >0) sb.append(", ");
sb.append (String.format("S-%02d", startseat + i]);

seats = sb.tostring() ;

trip.setBookedseats(trip.getBookedseats() + dto.getBeatCount());
tripRepository.save(trip) ;

String reference = "BK-2026-" + (1000 + (long) (Math. random() * 9000)) ;
Booking booking - new Booking () ;
booking. setBookingReference(reference) ;
booking. setUser (customer) ;
booking. setTrip(trip) ;
booking. setSeatCount (dto.getSeatCount () ) ;
booking. setSeatNumbers (seats) ;
booking. setTotalPrice(totalPrice) ;
booking. setDiscountAmount (discountAmount) ;
booking. setFinalPrice(finalPrice) ;
booking. setBookingDate (LocalDateTime.now () ) ;
booking. setStatus ( "CONFIRMED") ;
booking. setPaymentMethod (dto. getPaymentMethod () ) ;
booking. setNotes (dto.getNotes () ) ;
booking. setPromotion (appliedPromo);

if (dto.getBookingOfficerId() !- null) {
BookingOfficer officer - bookingOfficerRepository.findById(dto.getBookingOfficerId()) .orElse (nul1) ;
booking. setBookingOfficer (officer) ;

return bookingRepository. save (booking) ;

coverride
public List<Booking> getAllBookings () {
return bookingRepository. findAll () ;

@Override
public List<Booking> getBookingsByUser (Long userId) {
return bookingRepository. findeyUserId (userId) ;

@Override
public Booking getBookingById (Long id) {
return bookingRepository. findById (id)
.orElseThrow(() -> new ResourceNotFoundException ("Booking not found with ID: " + id) ) ;

@Override
public Booking getBookingByReference(String reference) {
return bookingRepository. findByBookingReference (reference)
.orElseThrow( () -> new ResourceNotFoundException ("Booking not found with reference: " + reference) ) ;

@Override
public Booking cancelBooking (Long id) {
return cancelBookingWithReason (id, "Cancelled by user/desk officer");

@Override
public Booking cancelBookingWithReason (Long id, String reason) {
Booking booking - getBookingById (id) ;
if ("CANCELLED".equalsIgnoreCase (booking.getStatus ()) ) {
throw new BusinessRuleException ("Booking is already cancelled! ") ;

booking. setStatus ("CANCELLED");
booking. setCancellationReason (reason) ;

Trip trip - booking.getTrip();
trip.setBookedSeats (Math.max(0, trip.getBookedSeats () - booking.getSeatCount ()));
tripRepository.save(trip) ;

return bookingRepository.save (booking) ;

@Override
public Booking updateBookingStatus (Long id, String status)
Booking booking = getBookingById(id);
booking. setStatus (status) ;
return bookingRepository. save (booking) ;

السيسة

@Override
public Booking validateCustomerBooking (Long id, String validationStatus)
Booking booking = getBookingById(id) ;
booking. setValidationStatus (validationStatus) ;
return bookingRepository. save (booking) ;
}
