package com.boatsafari.controller;

import com.boatsafari.model.Boat;
import com.boatsafari.model.MaintenanceLog;
import com.boatsafari.service.BoatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boats")
@CrossOrigin(origins = "*")
public class BoatController {
  @Autowired
  private BoatService boatService;
  @PostMapping
  public ResponseEntity<Boat> createBoat(@RequestParam(required = false) Long fleetManagerId, @RequestBody Boat boat) {
    return new ResponseEntity<>(boatService.createBoat(fleetManagerId, boat), HttpStatus.CREATED);
  }
  @GetMapping
  public ResponseEntity<List<Boat>> getAllBoats() {
    return ResponseEntity.ok(boatService.getAllBoats());
  }
  @GetMapping("/{id}")
  public ResponseEntity<Boat> getBoatById(@PathVariable Long id) {
    return ResponseEntity.ok(boatService.getBoatById(id));
  }
  @PutMapping("/{id}")
  public ResponseEntity<Boat> updateBoat(@PathVariable Long id, @RequestBody Boat boat) {
    return ResponseEntity.ok(boatService.updateBoat(id, boat));
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBoat(@PathVariable Long id) {
    boatService.deleteBoat(id);
    return ResponseEntity.noContent().build();
  }
  @PostMapping("/{id}/maintenance")
  public ResponseEntity<MaintenanceLog> addMaintenanceLog(@PathVariable Long id, @RequestBody MaintenanceLog log) {
    return new ResponseEntity<>(boatService.addMaintenanceLog(id, log), HttpStatus.CREATED);
  }
  @GetMapping("/{id}/maintenance")
  public ResponseEntity<List<MaintenanceLog>> getMaintenanceLogs(@PathVariable Long id) {
    return ResponseEntity.ok(boatService.getMaintenanceLogsForBoat(id));
  }
  @GetMapping("/maintenance/all")
  public ResponseEntity<List<MaintenanceLog>> getAllMaintenanceLogs() {
    return ResponseEntity.ok(boatService.getAllMaintenanceLogs());
  }
  @PutMapping("/maintenance/{logId}")
  public ResponseEntity<MaintenanceLog> updateMaintenanceLog(@PathVariable Long logId, @RequestBody MaintenanceLog log) {
    return ResponseEntity.ok(boatService.updateMaintenanceLog(logId, log));
  }
  @DeleteMapping("/maintenance/{logId}")
  public ResponseEntity<Void> deleteMaintenanceLog(@PathVariable Long logId) {
    boatService.deleteMaintenanceLog(logId);
    return ResponseEntity.noContent().build();
  }
}
