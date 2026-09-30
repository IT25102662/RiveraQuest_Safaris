package com.boatsafari.repository;

import com.boatsafari.model.BookingOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingOfficerRepository extends JpaRepository<BookingOfficer, Long> {
}