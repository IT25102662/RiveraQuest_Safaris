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
  private static final int MAX_BOAT_CAPACITY = 100;

  private void validateBoat(Boat boat) {
    if (boat.getName() == null || boat.getName().trim().isEmpty()) {
      throw new BusinessRuleException("Boat name is required.");
    }
    if (boat.getCapacity() == null || boat.getCapacity() < 1 || boat.getCapacity() > MAX_BOAT_CAPACITY) {
      throw new BusinessRuleException("Boat capacity must be between 1 and " + MAX_BOAT_CAPACITY + " passengers.");
    }
    if (boat.getFuelLevelPercentage() != null
        && (boat.getFuelLevelPercentage() < 0 || boat.getFuelLevelPercentage() > 100)) {
      throw new BusinessRuleException("Fuel level must be between 0 and 100 percent.");
    }
    if (boat.getCrewCount() != null && boat.getCrewCount() < 0) {
      throw new BusinessRuleException("Crew count cannot be negative.");
    }
  }

  @Override
  public Boat createBoat(Long fleetManagerId, Boat boat) {
    validateBoat(boat);
    if (boat.getRegistrationNumber() == null || boat.getRegistrationNumber().trim().isEmpty()) {
      throw new BusinessRuleException("Boat registration number is required.");
    }
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
    validateBoat(updatedBoat);
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

    // Returning a boat to service closes its open maintenance jobs so the logs stay consistent.
    if ("AVAILABLE".equalsIgnoreCase(existing.getStatus())) {
      for (MaintenanceLog log : maintenanceLogRepository.findByBoatId(id)) {
        if ("IN_PROGRESS".equalsIgnoreCase(log.getStatus())) {
          log.setStatus("COMPLETED");
          if (log.getMaintenanceDate() != null && log.getMaintenanceDate().isAfter(LocalDate.now())) {
            log.setMaintenanceDate(LocalDate.now());
          }
          maintenanceLogRepository.save(log);
        }
      }
    }

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
    maintenanceLogRepository.deleteAll(maintenanceLogRepository.findByBoatId(id));
    boatRepository.delete(existing);
}
  private static final java.util.List<String> LOG_STATUSES = java.util.List.of("SCHEDULED", "IN_PROGRESS", "COMPLETED");

  private void validateMaintenanceLog(MaintenanceLog log) {
    if (log.getDescription() == null || log.getDescription().trim().isEmpty()) {
      throw new BusinessRuleException("Maintenance description is required!");
    }
    if (log.getMaintenanceDate() == null) {
      throw new BusinessRuleException("Maintenance date is required!");
    }
    if (log.getCost() != null && log.getCost() < 0) {
      throw new BusinessRuleException("Maintenance cost cannot be negative!");
    }
    if (log.getStatus() == null || !LOG_STATUSES.contains(log.getStatus().toUpperCase())) {
      throw new BusinessRuleException("Status must be SCHEDULED, IN_PROGRESS or COMPLETED!");
    }
    log.setStatus(log.getStatus().toUpperCase());
    if ("COMPLETED".equals(log.getStatus()) && log.getMaintenanceDate().isAfter(LocalDate.now())) {
      throw new BusinessRuleException("A completed service cannot be dated in the future!");
    }
  }

  // Puts the boat back in service when its last IN_PROGRESS job is closed or removed.
  private void releaseBoatIfNoActiveJobs(Boat boat) {
    boolean stillActive = maintenanceLogRepository.findByBoatId(boat.getId()).stream()
      .anyMatch(l -> "IN_PROGRESS".equalsIgnoreCase(l.getStatus()));
    if (!stillActive && "MAINTENANCE".equalsIgnoreCase(boat.getStatus())) {
      boat.setStatus("AVAILABLE");
    }
  }

  @Override
  public MaintenanceLog addMaintenanceLog(Long boatId, MaintenanceLog log) {
    Boat boat = getBoatById(boatId);
    if (log.getMaintenanceDate() == null) {
      log.setMaintenanceDate(LocalDate.now());
    }
    if (log.getStatus() == null) {
      log.setStatus("COMPLETED");
    }
    validateMaintenanceLog(log);
    log.setBoat(boat);
    if (!"SCHEDULED".equals(log.getStatus())) {
      boat.setLastMaintenanceDate(log.getMaintenanceDate());
    }
    if ("IN_PROGRESS".equals(log.getStatus())) {
      boat.setStatus("MAINTENANCE");
    }
    boatRepository.save(boat);
    return maintenanceLogRepository.save(log);
  }

  @Override
  public MaintenanceLog updateMaintenanceLog(Long logId, MaintenanceLog updated) {
    MaintenanceLog existing = maintenanceLogRepository.findById(logId)
      .orElseThrow(() -> new ResourceNotFoundException("Maintenance log not found with ID: " + logId));
    validateMaintenanceLog(updated);
    boolean wasActive = "IN_PROGRESS".equalsIgnoreCase(existing.getStatus());
    existing.setMaintenanceDate(updated.getMaintenanceDate());
    existing.setDescription(updated.getDescription().trim());
    existing.setCost(updated.getCost());
    existing.setPerformedBy(updated.getPerformedBy());
    existing.setStatus(updated.getStatus());
    MaintenanceLog saved = maintenanceLogRepository.save(existing);
    Boat boat = existing.getBoat();
    if (!"SCHEDULED".equals(saved.getStatus())) {
      boat.setLastMaintenanceDate(saved.getMaintenanceDate());
    }
    if ("IN_PROGRESS".equals(saved.getStatus())) {
      boat.setStatus("MAINTENANCE");
    } else if (wasActive) {
      releaseBoatIfNoActiveJobs(boat);
    }
    boatRepository.save(boat);
    return saved;
  }

  @Override
  public void deleteMaintenanceLog(Long logId) {
    MaintenanceLog existing = maintenanceLogRepository.findById(logId)
      .orElseThrow(() -> new ResourceNotFoundException("Maintenance log not found with ID: " + logId));
    Boat boat = existing.getBoat();
    boolean wasActive = "IN_PROGRESS".equalsIgnoreCase(existing.getStatus());
    maintenanceLogRepository.delete(existing);
    if (wasActive) {
      releaseBoatIfNoActiveJobs(boat);
      boatRepository.save(boat);
    }
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
