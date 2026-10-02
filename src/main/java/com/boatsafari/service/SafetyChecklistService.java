package com.boatsafari.service;
import com.boatsafari.model.SafetyChecklist;
import java.util.List;
public interface SafetyChecklistService {
    SafetyChecklist createOrUpdateChecklist(Long tripId, SafetyChecklist checklist);
    SafetyChecklist getChecklistByTrip(Long tripId);
    List<SafetyChecklist> getAllChecklists();
    SafetyChecklist approveDeparture(Long tripId, String inspectorName);
}
