package com.boatsafari.service;

import com.boatsafari.model.Promotion;
import com.boatsafari.model.Trip;

import java.util.List;

public interface PromotionService {
    Promotion createPromotion(Long marketingOfficerId, Promotion promotion);
    List<Promotion> getAllPromotions();
    Promotion getPromotionById(Long id);
    Promotion getPromotionByCode(String code);
    Promotion updatePromotion(Long id, Promotion promotion);
    void deletePromotion(Long id);
    List<Promotion> searchPromotions(String keyword, String statusFilter);
    Promotion setApplicableTrips(Long promotionId, List<Long> tripIds);
    List<Long> getApplicableTripIds(Long promotionId);

    /** Validates every voucher rule for this trip and total, and returns the discount amount in LKR. */
    double calculateDiscount(Promotion promotion, Trip trip, double totalPrice);
}
