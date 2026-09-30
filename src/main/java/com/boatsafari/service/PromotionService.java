package com.boatsafari.service;

import com.boatsafari.model.Promotion;

import java.util.List;

public interface PromotionService {
    Promotion createPromotion(Long marketingOfficerId, Promotion promotion);
    List<Promotion> getAllPromotions();
    Promotion getPromotionById(Long id);
    Promotion getPromotionByCode(String code);
    Promotion updatePromotion(Long id, Promotion promotion);
    void deletePromotion(Long id);
    List<Promotion> searchPromotions(String keyword, String statusFilter);
}