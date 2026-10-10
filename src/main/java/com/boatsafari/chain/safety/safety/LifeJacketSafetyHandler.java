package com.boatsafari.chain.safety.safety;

import com.boatsafari.chain.safety.SafetyCheckHandler;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
import org.springframework.stereotype.Component;

/**
 * Concrete Handler 2: Verifies Life Jacket Audit and Passenger Capacity Threshold.
 * Ensures life jackets are inspected and the count is sufficient for booked passengers.
 */
@Component
public class LifeJacketSafetyHandler extends SafetyCheckHandler {

    @Override
    public boolean check(SafetyChecklist checklist, Trip trip, boolean strict) {
        if (!Boolean.TRUE.equals(checklist.getLifeJacketsChecked())) {
            if (strict) {
                throw new BusinessRuleException("Safety Violation: Life jackets audit must be completed before approving departure!");
            }
            return false;
        }

        int booked = (trip == null || trip.getBookedSeats() == null) ? 0 : trip.getBookedSeats();
        int required = Math.max(1, booked);

        if (checklist.getLifeJacketCount() == null || checklist.getLifeJacketCount() < required) {
            if (strict) {
                int found = checklist.getLifeJacketCount() == null ? 0 : checklist.getLifeJacketCount();
                throw new BusinessRuleException("Safety Violation: At least " + required + " life jacket(s) are required for this trip (found " + found + ")!");
            }
            return false;
        }

        return checkNext(checklist, trip, strict);
    }
}
