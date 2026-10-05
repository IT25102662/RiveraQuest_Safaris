package com.boatsafari.strategy;

import com.boatsafari.model.Promotion;

/**
 * Concrete strategy: fixed amount voucher (for example LKR 2,000 off).
 */
public class FixedAmountDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculate(Promotion promotion, double totalPrice) {
        return promotion.getFixedAmount() == null ? 0.0 : promotion.getFixedAmount();
    }
}
