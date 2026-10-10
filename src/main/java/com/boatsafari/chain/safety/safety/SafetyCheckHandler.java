package com.boatsafari.chain.safety.safety;

import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;

/**
 * Chain of Responsibility Pattern: Base Handler for Safety Verification.
 * Each handler verifies a specific safety compliance rule before trip departure.
 */
public abstract class SafetyCheckHandler {

    protected SafetyCheckHandler nextHandler;

    public SafetyCheckHandler setNext(SafetyCheckHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    /**
     * Checks safety compliance.
     * 
     * @param checklist Safety checklist being evaluated
     * @param trip Associated safari trip
     * @param strict If true, throws BusinessRuleException on failure (used for approveDeparture)
     * @return true if passed, false if violation found in non-strict mode
     */
    public abstract boolean check(SafetyChecklist checklist, Trip trip, boolean strict);

    protected boolean checkNext(SafetyChecklist checklist, Trip trip, boolean strict) {
        if (nextHandler == null) {
            return true;
        }
        return nextHandler.check(checklist, trip, strict);
    }
}
