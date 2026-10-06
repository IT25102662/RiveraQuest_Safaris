package com.boatsafari.report;

import com.boatsafari.model.Booking;
import com.boatsafari.repository.BookingRepository;

import java.util.List;

/**
 * Intermediate template for reports that are calculated from active (non-cancelled) bookings.
 * It implements the shared "load data" step once, so the revenue and summary reports reuse it.
 */
public abstract class ActiveBookingReportGenerator extends ReportGenerator<List<Booking>> {

    private final BookingRepository bookingRepository;

    protected ActiveBookingReportGenerator(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    protected List<Booking> loadData() {
        return bookingRepository.findAll().stream()
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .toList();
    }
}
