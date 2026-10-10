package com.boatsafari.chain.safety.safety;

import com.boatsafari.chain.safety.SafetyCheckHandler;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
import org.springframework.stereotype.Component;

/**
 * Concrete Handler 1: Verifies Weather Advisory Conditions.
 * Ensures departures are not approved under SEVERE_WARNING conditions.
 */
@Component
public class WeatherSafetyHandler extends SafetyCheckHandler {

    @Override
    public boolean check(SafetyChecklist checklist, Trip trip, boolean strict) {
        if ("SEVERE_WARNING".equalsIgnoreCase(checklist.getWeatherAdvisoryStatus())) {
            if (strict) {
                throw new BusinessRuleException("Safety Violation: Cannot approve departure under SEVERE_WARNING weather advisory!");
            }
            return false;
        }
        return checkNext(checklist, trip, strict);
    }
}
