package com.boatsafari.repository;

import com.boatsafari.model.Boat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoatRepository extends JpaRepository<Boat, Long> {
    Optional<Boat> findByRegistrationNumber(String registrationNumber);
    List<Boat> findByStatus(String status);
}
