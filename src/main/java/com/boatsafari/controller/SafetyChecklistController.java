package com.boatsafari.controller;
import com.boatsafari.model.SafetyChecklist;
import com.boatsafari.service.SafetyChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/safety")
@CrossOrigin(origins = "*")
public class SafetyChecklistController {
  
  @Autowired
  private SafetyChecklistService safetyChecklistService;
  @PostMapping("/trip/{tripId}")
  public ResponseEntity<SafetyChecklist> saveChecklist(@PathVariable Long tripId, @RequestBody SafetyChecklist checklist) {
      return ResponseEntity.ok(safetyChecklistService.createOrUpdateChecklist(tripId, checklist));
  }
  @GetMapping("/trip/{tripId}")
  public ResponseEntity<SafetyChecklist> getChecklistByTrip(@PathVariable Long tripId) {
      return ResponseEntity.ok(safetyChecklistService.getChecklistByTrip(tripId));
  }
  @GetMapping
  public ResponseEntity<List<SafetyChecklist>> getAllChecklists() {
      return ResponseEntity.ok(safetyChecklistService.getAllChecklists());
  }
  @PutMapping("/trip/{tripId}/approve")
  public ResponseEntity<SafetyChecklist> approveDeparture(@PathVariable Long tripId, @RequestParam String inspectorName) {
      return ResponseEntity.ok(safetyChecklistService.approveDeparture(tripId, inspectorName));
  }
  @PutMapping("/trip/{tripId}/hold")
  public ResponseEntity<SafetyChecklist> holdDeparture(@PathVariable Long tripId, @RequestParam String inspectorName, @RequestParam String reason) {
      return ResponseEntity.ok(safetyChecklistService.holdDeparture(tripId, inspectorName, reason));
  }
  @DeleteMapping("/trip/{tripId}")
  public ResponseEntity<Void> deleteChecklist(@PathVariable Long tripId) {
      safetyChecklistService.deleteChecklist(tripId);
      return ResponseEntity.noContent().build();
  }
}
