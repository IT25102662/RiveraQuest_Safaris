package com.boatsafari.repository;

import com.boatsafari.model.SafetyChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SafetyChecklistRepository extends JpaRepository<SafetyChecklist, Long> {
    Optional<SafetyChecklist> findByTripId(Long tripId);
}