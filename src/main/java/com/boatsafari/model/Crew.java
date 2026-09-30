package com.boatsafari.model;

import jakarta.persistence.*;

@Entity
@Table(name = "crews")
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false, unique = true, length = 30)
    private String licenseNumber;

    public Crew() {}

    public Crew(Long id, String name, String role, String licenseNumber) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.licenseNumber = licenseNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
}