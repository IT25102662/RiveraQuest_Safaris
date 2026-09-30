package com.boatsafari.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "trips")
@Check(constraints = "arrival_time > departure_time AND price >= 0 AND passenger_capacity > 0 AND status IN ('Scheduled', 'Cancelled', 'Completed')")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate tripDate;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column(nullable = false)
    private LocalTime arrivalTime;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer passengerCapacity;

    @Column(nullable = false, length = 15)
    private String status = "Scheduled";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boat_id", nullable = false)
    private Boat boat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guide_id")
    private Guide guide;

    // Extra, beyond the report — backs the interactive seat picker you chose to keep
    @Column(nullable = false)
    private Integer bookedSeats = 0;

    public Trip() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getTripDate() { return tripDate; }
    public void setTripDate(LocalDate tripDate) { this.tripDate = tripDate; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(Integer passengerCapacity) { this.passengerCapacity = passengerCapacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boat getBoat() { return boat; }
    public void setBoat(Boat boat) { this.boat = boat; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }

    public Integer getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(Integer bookedSeats) { this.bookedSeats = bookedSeats; }

    @Transient
    public Integer getAvailableSeats() {
        return passengerCapacity - (bookedSeats == null ? 0 : bookedSeats);
    }
}