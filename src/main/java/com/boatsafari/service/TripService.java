package com.boatsafari.service;

import com.boatsafari.model.Trip;

import java.util.List;

public interface TripService {
    Trip updateTrip(Long id, Long boatId, Long routeId, Long guideId, Trip updatedTrip);
    Trip createTrip(Long boatId, Long routeId, Long guideId, Trip trip);
    List<Trip> getAllTrips();
    List<Trip> getTripsByRoute(Long routeId);
    Trip getTripById(Long id);
    Trip updateTripStatus(Long id, String status);
    void deleteTrip(Long id);
}