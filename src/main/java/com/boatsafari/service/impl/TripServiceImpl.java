package com.boatsafari.service.impl;

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

import java.util.List;

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
        trip.setStatus(status);
        return tripRepository.save(trip);
    }

    @Override
    public void deleteTrip(Long id) {
        tripRepository.delete(getTripById(id));
    }
}