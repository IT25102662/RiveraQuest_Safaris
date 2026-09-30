package com.boatsafari.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fleet_managers")
@DiscriminatorValue("FleetManager")
public class FleetManager extends Staff {

    @Column(nullable = false, unique = true, length = 30)
    private String certificationNumber;

    public FleetManager() {}

    public String getCertificationNumber() { return certificationNumber; }
    public void setCertificationNumber(String certificationNumber) { this.certificationNumber = certificationNumber; }
}