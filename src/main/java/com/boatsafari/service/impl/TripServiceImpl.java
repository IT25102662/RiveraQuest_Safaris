package com.boatsafari.service.impl;

import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Boat;
import com.boatsafari.model.Guide;
import com.boatsafari.model.Route;
import com.boatsafari.model.Trip;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.GuideRepository;
import com.boatsafari.repository.RouteRepository;
import com.boatsafari.repository.TripRepository;
import com.boatsafari.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Override
    public Trip createTrip(Long boatId, Long routeId, Long guideId, Trip trip) {
        Boat boat = boatRepository.findById(boatId)
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found with ID: " + boatId));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + routeId));

        validateTrip(trip, boat, guideId, null);

        trip.setBoat(boat);
        trip.setRoute(route);

        if (guideId != null) {
            Guide guide = guideRepository.findById(guideId)
                    .orElseThrow(() -> new ResourceNotFoundException("Guide not found with ID: " + guideId));
            trip.setGuide(guide);
        }

        if (trip.getPassengerCapacity() == null) {
            trip.setPassengerCapacity(boat.getCapacity());
        }

        return tripRepository.save(trip);
    }

    /** Business rules for creating or editing a trip. 'existing' is null when creating. */
    private void validateTrip(Trip trip, Boat boat, Long guideId, Trip existing) {
        if (trip.getTripDate() == null) {
            throw new BusinessRuleException("Trip date is required.");
        }
        if (trip.getDepartureTime() == null || trip.getArrivalTime() == null) {
            throw new BusinessRuleException("Departure and arrival times are required.");
        }
        boolean dateChanged = existing == null || !trip.getTripDate().equals(existing.getTripDate());
        if (dateChanged && trip.getTripDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Trip date cannot be in the past.");
        }
        if (!trip.getArrivalTime().isAfter(trip.getDepartureTime())) {
            throw new BusinessRuleException("Arrival time must be after the departure time.");
        }
        if (trip.getPrice() == null || trip.getPrice() <= 0) {
            throw new BusinessRuleException("Price per seat must be greater than 0.");
        }

        boolean boatChanged = existing == null || existing.getBoat() == null
                || !existing.getBoat().getId().equals(boat.getId());
        String boatStatus = boat.getStatus() == null ? "" : boat.getStatus();
        if (boatChanged && ("MAINTENANCE".equalsIgnoreCase(boatStatus) || "DECOMMISSIONED".equalsIgnoreCase(boatStatus))) {
            throw new BusinessRuleException("Boat '" + boat.getName() + "' is not available for trips (status: "
                    + boatStatus.toUpperCase() + ").");
        }

        Integer capacity = trip.getPassengerCapacity();
        boolean capacityChanged = existing == null || !Objects.equals(capacity, existing.getPassengerCapacity());
        if (capacity != null && capacityChanged) {
            if (capacity < 1) {
                throw new BusinessRuleException("Passenger capacity must be at least 1.");
            }
            if (boat.getCapacity() != null && capacity > boat.getCapacity()) {
                throw new BusinessRuleException("Passenger capacity (" + capacity
                        + ") cannot exceed the boat's capacity (" + boat.getCapacity() + ").");
            }
        }

        if (!"Cancelled".equalsIgnoreCase(trip.getStatus())) {
            for (Trip other : tripRepository.findByBoatIdAndTripDate(boat.getId(), trip.getTripDate())) {
                if (isOtherActiveOverlap(trip, other, existing)) {
                    throw new BusinessRuleException("Boat '" + boat.getName() + "' is already scheduled on "
                            + other.getTripDate() + " from " + other.getDepartureTime() + " to " + other.getArrivalTime() + ".");
                }
            }
            if (guideId != null) {
                for (Trip other : tripRepository.findByGuideIdAndTripDate(guideId, trip.getTripDate())) {
                    if (isOtherActiveOverlap(trip, other, existing)) {
                        throw new BusinessRuleException("This guide is already assigned on " + other.getTripDate()
                                + " from " + other.getDepartureTime() + " to " + other.getArrivalTime() + ".");
                    }
                }
            }
        }
    }

    private boolean isOtherActiveOverlap(Trip trip, Trip other, Trip existing) {
        if (existing != null && other.getId().equals(existing.getId())) return false;
        if ("Cancelled".equalsIgnoreCase(other.getStatus())) return false;
        return trip.getDepartureTime().isBefore(other.getArrivalTime())
                && trip.getArrivalTime().isAfter(other.getDepartureTime());
    }

    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Override
    public List<Trip> getTripsByRoute(Long routeId) {
        return tripRepository.findByRouteId(routeId);
    }

    @Override
    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + id));
    }

    @Override
    public Trip updateTrip(Long id, Long boatId, Long routeId, Long guideId, Trip updatedTrip) {
        Trip existing = getTripById(id);

        Boat boat = boatRepository.findById(boatId)
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found with ID: " + boatId));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with ID: " + routeId));

        validateTrip(updatedTrip, boat, guideId, existing);

        existing.setBoat(boat);
        existing.setRoute(route);
        if (guideId != null) {
            Guide guide = guideRepository.findById(guideId)
                    .orElseThrow(() -> new ResourceNotFoundException("Guide not found with ID: " + guideId));
            existing.setGuide(guide);
        } else {
            existing.setGuide(null);
        }

        existing.setTripDate(updatedTrip.getTripDate());
        existing.setDepartureTime(updatedTrip.getDepartureTime());
        existing.setArrivalTime(updatedTrip.getArrivalTime());
        existing.setPrice(updatedTrip.getPrice());
        existing.setPassengerCapacity(updatedTrip.getPassengerCapacity());
        existing.setStatus(updatedTrip.getStatus());

        return tripRepository.save(existing);
    }
    @Override
    public Trip updateTripStatus(Long id, String status) {
        Trip trip = getTripById(id);
        String canonical = null;
        for (String allowed : List.of("Scheduled", "Completed", "Cancelled")) {
            if (allowed.equalsIgnoreCase(status == null ? "" : status.trim())) canonical = allowed;
        }
        if (canonical == null) {
            throw new BusinessRuleException("Invalid trip status '" + status
                    + "'. Allowed values: Scheduled, Completed, Cancelled.");
        }
        trip.setStatus(canonical);
        return tripRepository.save(trip);
    }

    @Override
    public void deleteTrip(Long id) {
        tripRepository.delete(getTripById(id));
    }
}