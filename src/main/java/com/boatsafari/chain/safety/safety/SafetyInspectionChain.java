package com.boatsafari.chain.safety.safety;

import com.boatsafari.chain.safety.EmergencyContactSafetyHandler;
import com.boatsafari.chain.safety.FirstAidKitSafetyHandler;
import com.boatsafari.chain.safety.LifeJacketSafetyHandler;
import com.boatsafari.chain.safety.SafetyCheckHandler;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Chain of Responsibility Pattern: Safety Inspection Chain Coordinator.
 * Sets up and executes the sequence of safety verification handlers.
 */
@Component
public class SafetyInspectionChain {

    @Autowired
    private WeatherSafetyHandler weatherSafetyHandler;

    @Autowired
    private LifeJacketSafetyHandler lifeJacketSafetyHandler;

    @Autowired
    private FirstAidKitSafetyHandler firstAidKitSafetyHandler;

    @Autowired
    private EmergencyContactSafetyHandler emergencyContactSafetyHandler;

    private SafetyCheckHandler rootHandler;

    @PostConstruct
    public void initChain() {
        // Build the Chain: Weather -> LifeJackets -> FirstAidKit -> EmergencyContact
        weatherSafetyHandler.setNext(lifeJacketSafetyHandler)
                            .setNext(firstAidKitSafetyHandler)
                            .setNext(emergencyContactSafetyHandler);
        
        this.rootHandler = weatherSafetyHandler;
    }

    /**
     * Executes the entire safety verification chain.
     * 
     * @param checklist Safety checklist being evaluated
     * @param trip Associated safari trip
     * @param strict If true, throws exception immediately on first violation
     * @return true if all safety rules in the chain passed
     */
    public boolean executeChain(SafetyChecklist checklist, Trip trip, boolean strict) {
        if (rootHandler == null) {
            initChain();
        }
        return rootHandler.check(checklist, trip, strict);
    }
}
