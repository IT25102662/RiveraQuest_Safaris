package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "boats")
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String status = "AVAILABLE"; // AVAILABLE, MAINTENANCE, IN_USE, DECOMMISSIONED

    private String engineStatus = "GOOD"; // GOOD, NEEDS_SERVICE, CRITICAL

    private Double fuelLevelPercentage = 100.0;

    private String captainName;

    private Integer crewCount = 2;

    private LocalDate lastMaintenanceDate;

    // Report-required fields, added alongside your existing operational fields
    private String engineType;

    @Column(nullable = false, length = 15)
    private String safetyStatus = "Cleared";

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fleet_manager_id")
    private FleetManager fleetManager;

    public Boat() {}

    public Boat(Long id, String name, String registrationNumber, Integer capacity, String status, String engineStatus, Double fuelLevelPercentage, String captainName, Integer crewCount, LocalDate lastMaintenanceDate) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.capacity = capacity;
        this.status = status;
        this.engineStatus = engineStatus;
        this.fuelLevelPercentage = fuelLevelPercentage;
        this.captainName = captainName;
        this.crewCount = crewCount;
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEngineStatus() { return engineStatus; }
    public void setEngineStatus(String engineStatus) { this.engineStatus = engineStatus; }

    public Double getFuelLevelPercentage() { return fuelLevelPercentage; }
    public void setFuelLevelPercentage(Double fuelLevelPercentage) { this.fuelLevelPercentage = fuelLevelPercentage; }

    public String getCaptainName() { return captainName; }
    public void setCaptainName(String captainName) { this.captainName = captainName; }

    public Integer getCrewCount() { return crewCount; }
    public void setCrewCount(Integer crewCount) { this.crewCount = crewCount; }

    public LocalDate getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }

    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }

    public String getSafetyStatus() { return safetyStatus; }
    public void setSafetyStatus(String safetyStatus) { this.safetyStatus = safetyStatus; }

    public FleetManager getFleetManager() { return fleetManager; }
    public void setFleetManager(FleetManager fleetManager) { this.fleetManager = fleetManager; }
}