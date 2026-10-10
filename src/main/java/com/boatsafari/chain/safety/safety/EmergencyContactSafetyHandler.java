package com.boatsafari.chain.safety.safety;

import com.boatsafari.chain.safety.SafetyCheckHandler;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
import org.springframework.stereotype.Component;

/**
 * Concrete Handler 4: Verifies Emergency Contact Number Availability.
 * Ensures an active safety contact phone number is registered.
 */
@Component
public class EmergencyContactSafetyHandler extends SafetyCheckHandler {

    @Override
    public boolean check(SafetyChecklist checklist, Trip trip, boolean strict) {
        if (checklist.getEmergencyContactNumber() != null && checklist.getEmergencyContactNumber().trim().isEmpty()) {
            if (strict) {
                throw new BusinessRuleException("Safety Violation: Emergency contact number cannot be empty!");
            }
            return false;
        }
        return checkNext(checklist, trip, strict);
    }
}
