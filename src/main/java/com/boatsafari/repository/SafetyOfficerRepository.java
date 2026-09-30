package com.boatsafari.repository;

import com.boatsafari.model.SafetyOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SafetyOfficerRepository extends JpaRepository<SafetyOfficer, Long> {
}