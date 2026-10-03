package com.boatsafari.strategy;

import com.boatsafari.model.Promotion;

/**
 * Strategy interface: one way of calculating a voucher discount.
 * Every concrete discount type implements this contract.
 */
public interface DiscountStrategy {

    /**
     * Calculates the raw discount amount (LKR) for a booking total.
     * Voucher validity rules (dates, limits, trips) are checked elsewhere.
     */
    double calculate(Promotion promotion, double totalPrice);
}
