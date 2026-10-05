package com.boatsafari.service;
import com.boatsafari.model.FeedbackStatus;
import com.boatsafari.model.Review;
import java.util.List;
import java.util.Map;
public interface ReviewService {
  Review createReview(Long userId, Long tripId, Integer rating, String comment);
  List<Review> getReviewsForTrip(Long tripId);
  List<Review> getAllReviews();
  List<Review> searchReviews(String keyword, Integer ratingFilter, String statusFilter);
  Double getAverageRatingForTrip(Long tripId);
  Double getOverallAverageRating();
  Map<Integer, Long> getRatingBreakdown();
  Review updateStatus(Long id, FeedbackStatus status);
  void deleteReview(Long id);
}
