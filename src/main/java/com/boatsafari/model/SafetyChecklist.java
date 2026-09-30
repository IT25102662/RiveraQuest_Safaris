package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "safety_checklists")
public class SafetyChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    private Trip trip;

    @Column(nullable = false)
    private Boolean lifeJacketsChecked = true;

    private Integer lifeJacketCount = 20;

    @Column(nullable = false)
    private Boolean firstAidKitChecked = true;

    private String emergencyContactNumber = "+94 77 123 4567";

    @Column(nullable = false)
    private String weatherAdvisoryStatus = "CLEAR";

    @Column(nullable = false)
    private Boolean departureApproved = true;

    private String inspectorName;

    private LocalDateTime inspectionTime = LocalDateTime.now();

    private String comments;

    public SafetyChecklist() {}

    public SafetyChecklist(Long id, Trip trip, Boolean lifeJacketsChecked, Integer lifeJacketCount, Boolean firstAidKitChecked, String emergencyContactNumber, String weatherAdvisoryStatus, Boolean departureApproved, String inspectorName, LocalDateTime inspectionTime, String comments) {
        this.id = id;
        this.trip = trip;
        this.lifeJacketsChecked = lifeJacketsChecked;
        this.lifeJacketCount = lifeJacketCount;
        this.firstAidKitChecked = firstAidKitChecked;
        this.emergencyContactNumber = emergencyContactNumber;
        this.weatherAdvisoryStatus = weatherAdvisoryStatus;
        this.departureApproved = departureApproved;
        this.inspectorName = inspectorName;
        this.inspectionTime = inspectionTime;
        this.comments = comments;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Boolean getLifeJacketsChecked() { return lifeJacketsChecked; }
    public void setLifeJacketsChecked(Boolean lifeJacketsChecked) { this.lifeJacketsChecked = lifeJacketsChecked; }

    public Integer getLifeJacketCount() { return lifeJacketCount; }
    public void setLifeJacketCount(Integer lifeJacketCount) { this.lifeJacketCount = lifeJacketCount; }

    public Boolean getFirstAidKitChecked() { return firstAidKitChecked; }
    public void setFirstAidKitChecked(Boolean firstAidKitChecked) { this.firstAidKitChecked = firstAidKitChecked; }

    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public void setEmergencyContactNumber(String emergencyContactNumber) { this.emergencyContactNumber = emergencyContactNumber; }

    public String getWeatherAdvisoryStatus() { return weatherAdvisoryStatus; }
    public void setWeatherAdvisoryStatus(String weatherAdvisoryStatus) { this.weatherAdvisoryStatus = weatherAdvisoryStatus; }

    public Boolean getDepartureApproved() { return departureApproved; }
    public void setDepartureApproved(Boolean departureApproved) { this.departureApproved = departureApproved; }

    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }

    public LocalDateTime getInspectionTime() { return inspectionTime; }
    public void setInspectionTime(LocalDateTime inspectionTime) { this.inspectionTime = inspectionTime; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}