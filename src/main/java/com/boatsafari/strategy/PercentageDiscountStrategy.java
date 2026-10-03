package com.boatsafari.strategy;

import com.boatsafari.model.Promotion;

/**
 * Concrete strategy: percentage voucher (for example SAFARI15 = 15%).
 * An optional maximum discount caps the result.
 */
public class PercentageDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculate(Promotion promotion, double totalPrice) {
        double percentage = promotion.getDiscountPercentage() == null ? 0.0 : promotion.getDiscountPercentage();
        double discount = totalPrice * percentage / 100.0;

        Double maxDiscount = promotion.getMaxDiscount();
        if (maxDiscount != null && maxDiscount > 0) {
            discount = Math.min(discount, maxDiscount);
        }
        return discount;
    }
}
