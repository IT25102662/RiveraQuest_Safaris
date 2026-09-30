package com.boatsafari.service.impl;

import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Boat;
import com.boatsafari.model.FleetManager;
import com.boatsafari.model.MaintenanceLog;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.FleetManagerRepository;
import com.boatsafari.repository.MaintenanceLogRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.BoatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BoatServiceImpl implements BoatService {
  @Autowired
  private BoatRepository boatRepository;
  @Autowired
  private TripRepository tripRepository;
  @Autowired
  private MaintenanceLogRepository maintenanceLogRepository;
  @Autowired
  private FleetManagerRepository fleetManagerRepository;
  @Override
  public Boat createBoat(Long fleetManagerId, Boat boat) {
    if (boat.getRegistrationNumber() != null &&
        boatRepository.findByRegistrationNumber(boat.getRegistrationNumber()).isPresent()) {
      throw new BusinessRuleException("Boat with registration number " + boat.getRegistrationNumber() + " already exists!");
}
    if (fleetManagerId != null) {
      FleetManager fm = fleetManagerRepository.findById(fleetManagerId)
        .orElseThrow(() -> new ResourceNotFoundException("Fleet Manager not found with ID: " + fleetManagerId));
      boat.setFleetManager(fm);
} else {
      fleetManagerRepository.findAll().stream().findFirst().ifPresent(boat::setFleetManager);
}
    return boatRepository.save(boat);
}
  
  @Override
  public List<Boat> getAllBoats() {
    return boatRepository.findAll();
}
  
  @Override
  public Boat getBoatById(Long id) {
    return boatRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Boat not found with ID: " + id));
}
  
  @Override
  public Boat updateBoat(Long id, Boat updatedBoat) {
    Boat existing = getBoatById(id);
    if ("CRITICAL".equalsIgnoreCase(updatedBoat.getEngineStatus()) && "AVAILABLE".equalsIgnoreCase(updatedBoat.getStatus())) {
      throw new BusinessRuleException("Cannot set boat status to AVAILABLE when engine status is CRITICAL!");
}
    existing.setName(updatedBoat.getName());
    existing.setRegistrationNumber(updatedBoat.getRegistrationNumber());
    existing.setCapacity(updatedBoat.getCapacity());
    existing.setStatus(updatedBoat.getStatus());
    existing.setEngineStatus(updatedBoat.getEngineStatus());
    existing.setFuelLevelPercentage(updatedBoat.getFuelLevelPercentage());
    existing.setCaptainName(updatedBoat.getCaptainName());
    existing.setCrewCount(updatedBoat.getCrewCount());
    existing.setLastMaintenanceDate(updatedBoat.getLastMaintenanceDate());
    existing.setEngineType(updatedBoat.getEngineType());
    existing.setSafetyStatus(updatedBoat.getSafetyStatus());
    
    return boatRepository.save(existing);
}
  
  @Override
  public void deleteBoat(Long id) {
    Boat existing = getBoatById(id);
    boolean hasActiveTrips = tripRepository.findByBoatId(id).stream()
      .anyMatch(t -> "Scheduled".equals(t.getStatus()));
    if (hasActiveTrips) {
      throw new BusinessRuleException("Cannot delete boat assigned to active or scheduled trips!");
}
    boatRepository.delete(existing);
}
  @Override
  public MaintenanceLog addMaintenanceLog(Long boatId, MaintenanceLog log) {
    Boat boat = getBoatById(boatId);
    log.setBoat(boat);
    if (log.getMaintenanceDate() == null) {
      log.setMaintenanceDate(LocalDate.now());
}
    boat.setLastMaintenanceDate(log.getMaintenanceDate());
    if ("IN_PROGRESS".equalsIgnoreCase(log.getStatus())) {
      boat.setStatus("MAINTENANCE");
}
    boatRepository.save(boat);
    return maintenanceLogRepository.save(log);
}
  @Override
  public List<MaintenanceLog> getMaintenanceLogsForBoat(Long boatId) {
    return maintenanceLogRepository.findByBoatId(boatId);
}
  @Override
  public List<MaintenanceLog> getAllMaintenanceLogs() {
    return maintenanceLogRepository.findAll();
  }
}
