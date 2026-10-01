package com.boatsafari.service.impl;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.model.Trip;
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
return safetyChecklistRepository.save(itemToSave);
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
checklist.setDepartureApproved(true);
checklist.setInspectorName(inspectorName);
checklist.setInspectionTime(LocalDateTime.now());
return safetyChecklistRepository.save(checklist);
}
}
