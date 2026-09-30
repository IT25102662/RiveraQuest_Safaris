package com.boatsafari.model;

import jakarta.persistence.*;

@Entity
@Table(name = "administrators")
@DiscriminatorValue("Administrator")
public class Administrator extends Staff {

    @Column(nullable = false, length = 20)
    private String accessLevel;

    public Administrator() {}

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
}