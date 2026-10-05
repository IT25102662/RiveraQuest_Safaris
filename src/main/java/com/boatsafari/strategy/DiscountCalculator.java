package com.boatsafari.strategy;

import com.boatsafari.model.Promotion;

/**
 * Context class of the Strategy pattern.
 * It holds a reference to a DiscountStrategy and delegates the calculation to it,
 * without knowing which concrete algorithm is behind the interface.
 */
public class DiscountCalculator {

    private DiscountStrategy strategy;

    public DiscountCalculator(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    /** The strategy can be switched at runtime. */
    public void setStrategy(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    /** Returns the discount, never negative and never more than the booking total. */
    public double calculate(Promotion promotion, double totalPrice) {
        if (strategy == null) {
            throw new IllegalStateException("No discount strategy selected!");
        }
        double discount = strategy.calculate(promotion, totalPrice);
        return Math.max(0.0, Math.min(discount, totalPrice));
    }
}
