package com.boatsafari.controller;

import com.boatsafari.model.Trip;
import com.boatsafari.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public ResponseEntity<Trip> createTrip(
            @RequestParam Long boatId,
            @RequestParam Long routeId,
            @RequestParam(required = false) Long guideId,
            @RequestBody Trip trip) {
        return new ResponseEntity<>(tripService.createTrip(boatId, routeId, guideId, trip), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips(@RequestParam(required = false) Long routeId) {
        if (routeId != null) {
            return ResponseEntity.ok(tripService.getTripsByRoute(routeId));
        }
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Trip> updateTripStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(tripService.updateTripStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(
            @PathVariable Long id,
            @RequestParam Long boatId,
            @RequestParam Long routeId,
            @RequestParam(required = false) Long guideId,
            @RequestBody Trip trip) {
        return ResponseEntity.ok(tripService.updateTrip(id, boatId, routeId, guideId, trip));
    }
}