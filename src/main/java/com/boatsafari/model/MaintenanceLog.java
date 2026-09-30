package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_logs")
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boat_id", nullable = false)
    private Boat boat;

    @Column(nullable = false)
    private LocalDate maintenanceDate;

    @Column(nullable = false, length = 1000)
    private String description;

    private Double cost;

    private String performedBy;

    private String status = "COMPLETED"; // COMPLETED, IN_PROGRESS, SCHEDULED

    public MaintenanceLog() {}

    public MaintenanceLog(Long id, Boat boat, LocalDate maintenanceDate, String description, Double cost, String performedBy, String status) {
        this.id = id;
        this.boat = boat;
        this.maintenanceDate = maintenanceDate;
        this.description = description;
        this.cost = cost;
        this.performedBy = performedBy;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boat getBoat() { return boat; }
    public void setBoat(Boat boat) { this.boat = boat; }

    public LocalDate getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(LocalDate maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
