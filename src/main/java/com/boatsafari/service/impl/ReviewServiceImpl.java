package com.boatsafari.service.impl;

import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Booking;
import com.boatsafari.model.Customer;
import com.boatsafari.model.FeedbackStatus;
import com.boatsafari.model.Review;
import com.boatsafari.model.Trip;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.CustomerRepository;
import com.boatsafari.repository.ReviewRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {
  
  @Autowired
  private ReviewRepository reviewRepository;
  
  @Autowired
  private CustomerRepository customerRepository;
  
  @Autowired
  private TripRepository tripRepository;
  
  @Autowired
  private BookingRepository bookingRepository;
  
  @Override
  public Review createReview(Long userId, Long tripId, Integer rating, String comment) {
    Customer customer = customerRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + userId));
    
    Trip trip = tripRepository.findById(tripId)
      .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
    
    List<Booking> userBookings = bookingRepository.findByUserId(userId);
    boolean hasBooked = userBookings.stream()
      .anyMatch(b -> b.getTrip().getId().equals(tripId));
    
    if (!hasBooked) {
      throw new BusinessRuleException("Review Restriction: You can only submit a review for trips you have booked!");
    }
    
    if (rating < 1 || rating > 5) {
      throw new BusinessRuleException("Rating must be between 1 and 5 stars!");
    }
    
    if (comment == null || comment.trim().isEmpty()) {
      throw new BusinessRuleException("Review comment cannot be empty.");
    }
    
    Review review = new Review();
    review.setUser(customer);
    review.setTrip(trip);
    review.setRating(rating);
    review.setComment(comment);
    review.setReviewDate(LocalDateTime.now());
    review.setApproved(true);
    review.setStatus(FeedbackStatus.NEW);
    
    return reviewRepository.save(review);
  }
  
  @Override
  public List<Review> getReviewsForTrip(Long tripId) {
    return reviewRepository.findByTripId(tripId);
  }
  
  @Override
  public List<Review> getAllReviews() {
    return reviewRepository.findAll();
  }
  
  @Override
  public List<Review> searchReviews(String keyword, Integer ratingFilter, String statusFilter) {
    String kw = keyword == null ? "" : keyword.trim().toLowerCase();
    
    return reviewRepository.findAll().stream()
      .filter(r -> kw.isEmpty()
              || (r.getUser() != null && r.getUser().getFullName() != null && r.getUser().getFullName().toLowerCase().contains(kw))
              || (r.getComment() != null && r.getComment().toLowerCase().contains(kw)))
      .filter(r -> ratingFilter == null || ratingFilter == 0 || (r.getRating() != null && r.getRating().equals(ratingFilter)))
      .filter(r -> statusFilter == null || statusFilter.isEmpty() || "ALL".equalsIgnoreCase(statusFilter)
              || (r.getStatus() != null && r.getStatus().name().equalsIgnoreCase(statusFilter)))
       .toList();
}
  
  @Override
  public Double getAverageRatingForTrip(Long tripId) {
    List<Review> reviews = getReviewsForTrip(tripId);
    if (reviews.isEmpty()) return 0.0;
    return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
  }
  
  @Override
  public Double getOverallAverageRating() {
    List<Review> reviews = reviewRepository.findAll();
    if (reviews.isEmpty()) return 0.0;
    return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
  }
  
  @Override
  public Map<Integer, Long> getRatingBreakdown() {
    List<Review> reviews = reviewRepository.findAll();
    Map<Integer, Long> breakdown = new HashMap<>();
    for (int star = 1; star <= 5; star++) {
      final int s = star;
      breakdown.put(s, reviews.stream().filter(r -> r.getRating() != null && r.getRating() == s).count());
    }
    return breakdown;
  }
  
  @Override
  public Review updateStatus(Long id, FeedbackStatus status) {
    Review review = reviewRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));
    review.setStatus(status);
    return reviewRepository.save(review);
  }
  
  @Override
  public void deleteReview(Long id) {
    Review review = reviewRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));
    reviewRepository.delete(review);
  }
}
