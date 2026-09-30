package com.boatsafari.repository;

import com.boatsafari.model.FleetManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FleetManagerRepository extends JpaRepository<FleetManager, Long> {
}