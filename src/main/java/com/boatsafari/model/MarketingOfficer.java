package com.boatsafari.model;

import jakarta.persistence.*;

@Entity
@Table(name = "marketing_officers")
@DiscriminatorValue("MarketingOfficer")
public class MarketingOfficer extends Staff {

    @Column(nullable = false, length = 50)
    private String department;

    public MarketingOfficer() {}

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}