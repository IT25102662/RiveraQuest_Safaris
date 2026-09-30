package com.boatsafari.model;

import jakarta.persistence.*;

@Entity
@Table(name = "safety_officers")
@DiscriminatorValue("SafetyOfficer")
public class SafetyOfficer extends Staff {

    @Column(nullable = false, unique = true, length = 30)
    private String safetyLicenseNo;

    public SafetyOfficer() {}

    public String getSafetyLicenseNo() { return safetyLicenseNo; }
    public void setSafetyLicenseNo(String safetyLicenseNo) { this.safetyLicenseNo = safetyLicenseNo; }
}