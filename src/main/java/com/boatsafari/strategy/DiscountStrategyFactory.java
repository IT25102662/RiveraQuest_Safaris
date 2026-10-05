package com.boatsafari.strategy;

import com.boatsafari.model.DiscountType;

/**
 * Factory pattern: creates the correct DiscountStrategy for a voucher's discount type,
 * so callers never use "new" on a concrete strategy and never write if-else chains.
 */
public class DiscountStrategyFactory {

    public DiscountStrategy create(DiscountType type) {
        if (type == DiscountType.FIXED_AMOUNT) {
            return new FixedAmountDiscountStrategy();
        }
        // PERCENTAGE is the default for vouchers without an explicit type
        return new PercentageDiscountStrategy();
    }
}
