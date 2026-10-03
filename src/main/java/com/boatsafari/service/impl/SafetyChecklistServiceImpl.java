package com.boatsafari.service.impl;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Boat;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.SafetyChecklistRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.SafetyChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class SafetyChecklistServiceImpl implements SafetyChecklistService {
@Autowired
private SafetyChecklistRepository safetyChecklistRepository;
@Autowired
private TripRepository tripRepository;
@Autowired
private BoatRepository boatRepository;
@Override
public SafetyChecklist createOrUpdateChecklist(Long tripId, SafetyChecklist checklist) {
Trip trip = tripRepository.findById(tripId)
.orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
if (checklist.getLifeJacketCount() != null && checklist.getLifeJacketCount() < 0) {
throw new BusinessRuleException("Life jacket count cannot be negative!");
}
// Departure Safety Rule Enforcement
if ("SEVERE_WARNING".equalsIgnoreCase(checklist.getWeatherAdvisoryStatus())) {
checklist.setDepartureApproved(false);
} else if (!Boolean.TRUE.equals(checklist.getLifeJacketsChecked()) || !Boolean.TRUE.equals(checklist.getFirstAidKitChecked())) {
checklist.setDepartureApproved(false);
} else if (checklist.getLifeJacketCount() == null || checklist.getLifeJacketCount() < requiredLifeJackets(trip)) {
checklist.setDepartureApproved(false);
}
Optional<SafetyChecklist> existing = safetyChecklistRepository.findByTripId(tripId);
SafetyChecklist itemToSave = existing.orElse(checklist);
itemToSave.setTrip(trip);
itemToSave.setLifeJacketsChecked(checklist.getLifeJacketsChecked());
itemToSave.setLifeJacketCount(checklist.getLifeJacketCount());
itemToSave.setFirstAidKitChecked(checklist.getFirstAidKitChecked());
itemToSave.setEmergencyContactNumber(checklist.getEmergencyContactNumber());
itemToSave.setWeatherAdvisoryStatus(checklist.getWeatherAdvisoryStatus());
itemToSave.setDepartureApproved(checklist.getDepartureApproved());
itemToSave.setInspectorName(checklist.getInspectorName());
itemToSave.setInspectionTime(LocalDateTime.now());
itemToSave.setComments(checklist.getComments());
SafetyChecklist saved = safetyChecklistRepository.save(itemToSave);
syncBoatSafety(trip, Boolean.TRUE.equals(saved.getDepartureApproved()));
return saved;
}
@Override
public SafetyChecklist getChecklistByTrip(Long tripId) {
return safetyChecklistRepository.findByTripId(tripId)
.orElseThrow(() -> new ResourceNotFoundException("No safety checklist found for trip ID: " + tripId));
}
@Override
public List<SafetyChecklist> getAllChecklists() {
return safetyChecklistRepository.findAll();
}
@Override
public SafetyChecklist approveDeparture(Long tripId, String inspectorName) {
SafetyChecklist checklist = getChecklistByTrip(tripId);
if (!Boolean.TRUE.equals(checklist.getLifeJacketsChecked())) {
throw new BusinessRuleException("Safety Violation: Life jackets audit must be completed before approving departure!");
}
if (!Boolean.TRUE.equals(checklist.getFirstAidKitChecked())) {
throw new BusinessRuleException("Safety Violation: First aid kit must be checked before approving departure!");
}
if ("SEVERE_WARNING".equalsIgnoreCase(checklist.getWeatherAdvisoryStatus())) {
throw new BusinessRuleException("Safety Violation: Cannot approve departure under SEVERE_WARNING weather advisory!");
}
int required = requiredLifeJackets(checklist.getTrip());
if (checklist.getLifeJacketCount() == null || checklist.getLifeJacketCount() < required) {
throw new BusinessRuleException("Safety Violation: At least " + required + " life jacket(s) are required for this trip (found "
+ (checklist.getLifeJacketCount() == null ? 0 : checklist.getLifeJacketCount()) + ")!");
}
checklist.setDepartureApproved(true);
if (checklist.getComments() != null && checklist.getComments().startsWith("HOLD:")) {
checklist.setComments(null);
}
checklist.setInspectorName(inspectorName);
checklist.setInspectionTime(LocalDateTime.now());
SafetyChecklist saved = safetyChecklistRepository.save(checklist);
syncBoatSafety(saved.getTrip(), true);
return saved;
}

@Override
public SafetyChecklist holdDeparture(Long tripId, String inspectorName, String reason) {
SafetyChecklist checklist = getChecklistByTrip(tripId);
if (reason == null || reason.trim().isEmpty()) {
throw new BusinessRuleException("A reason is required to place a departure on hold!");
}
checklist.setDepartureApproved(false);
checklist.setInspectorName(inspectorName);
checklist.setInspectionTime(LocalDateTime.now());
checklist.setComments("HOLD: " + reason.trim());
SafetyChecklist saved = safetyChecklistRepository.save(checklist);
syncBoatSafety(saved.getTrip(), false);
return saved;
}
/** Minimum life jackets: one per booked passenger, and never fewer than 1. */
private int requiredLifeJackets(Trip trip) {
int booked = (trip == null || trip.getBookedSeats() == null) ? 0 : trip.getBookedSeats();
return Math.max(1, booked);
}

/** Keeps the boat's safety status in step with the latest departure decision. */
private void syncBoatSafety(Trip trip, boolean cleared) {
Boat boat = trip == null ? null : trip.getBoat();
if (boat != null) {
boat.setSafetyStatus(cleared ? "Cleared" : "Not Cleared");
boatRepository.save(boat);
}
}

@Override
public void deleteChecklist(Long tripId) {
SafetyChecklist checklist = getChecklistByTrip(tripId);
safetyChecklistRepository.delete(checklist);
}
}
