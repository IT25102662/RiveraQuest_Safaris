package com.boatsafari.report;

import com.boatsafari.model.Booking;
import com.boatsafari.repository.BookingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/** Concrete report: gross revenue, discounts and net revenue. */
@Component
public class RevenueReportGenerator extends ActiveBookingReportGenerator {

    public RevenueReportGenerator(BookingRepository bookingRepository) {
        super(bookingRepository);
    }

    @Override
    protected void buildReport(List<Booking> activeBookings, Map<String, Object> report) {
        double grossRevenue = activeBookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        double totalDiscounts = activeBookings.stream().mapToDouble(Booking::getDiscountAmount).sum();
        double netRevenue = activeBookings.stream().mapToDouble(Booking::getFinalPrice).sum();

        report.put("grossRevenue", grossRevenue);
        report.put("totalDiscounts", totalDiscounts);
        report.put("netRevenue", netRevenue);
        report.put("totalBookingsCount", activeBookings.size());
    }
}
