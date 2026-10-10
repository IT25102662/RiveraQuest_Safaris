package com.boatsafari.chain.safety.safety;

import com.boatsafari.chain.safety.SafetyCheckHandler;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
import org.springframework.stereotype.Component;

/**
 * Concrete Handler 3: Verifies Onboard First Aid Kit Inspection.
 * Ensures the emergency first aid kit is checked and fully operational.
 */
@Component
public class FirstAidKitSafetyHandler extends SafetyCheckHandler {

    @Override
    public boolean check(SafetyChecklist checklist, Trip trip, boolean strict) {
        if (!Boolean.TRUE.equals(checklist.getFirstAidKitChecked())) {
            if (strict) {
                throw new BusinessRuleException("Safety Violation: First aid kit must be checked before approving departure!");
            }
            return false;
        }
        return checkNext(checklist, trip, strict);
    }
}
