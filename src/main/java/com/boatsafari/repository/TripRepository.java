package com.boatsafari.repository;

import com.boatsafari.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByBoatId(Long boatId);
    List<Trip> findByStatus(String status);
}