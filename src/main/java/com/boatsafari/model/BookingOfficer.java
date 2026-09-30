package com.boatsafari.model;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_officers")
@DiscriminatorValue("BookingOfficer")
public class BookingOfficer extends Staff {

    @Column(nullable = false, length = 10)
    private String deskNumber;

    public BookingOfficer() {}

    public String getDeskNumber() { return deskNumber; }
    public void setDeskNumber(String deskNumber) { this.deskNumber = deskNumber; }
}