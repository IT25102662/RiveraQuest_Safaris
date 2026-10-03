package com.boatsafari.repository;

import com.boatsafari.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByBoatId(Long boatId);
    List<Trip> findByBoatIdAndTripDate(Long boatId, LocalDate tripDate);
    List<Trip> findByGuideIdAndTripDate(Long guideId, LocalDate tripDate);
    List<Trip> findByStatus(String status);
}