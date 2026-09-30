package com.boatsafari.repository;

import com.boatsafari.model.MarketingOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketingOfficerRepository extends JpaRepository<MarketingOfficer, Long> {
}