package com.boatsafari.report;

import com.boatsafari.model.Booking;
import com.boatsafari.repository.BoatRepository;
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.CustomerRepository;
import com.boatsafari.repository.StaffRepository;
import com.boatsafari.repository.TripRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/** Concrete report: system-wide totals for the Administrator dashboard. */
@Component
public class SystemSummaryReportGenerator extends ActiveBookingReportGenerator {

    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final BoatRepository boatRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public SystemSummaryReportGenerator(CustomerRepository customerRepository,
                                        StaffRepository staffRepository,
                                        BoatRepository boatRepository,
                                        TripRepository tripRepository,
                                        BookingRepository bookingRepository) {
        super(bookingRepository);
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.boatRepository = boatRepository;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    protected void buildReport(List<Booking> activeBookings, Map<String, Object> report) {
        report.put("totalCustomers", customerRepository.count());
        report.put("totalStaff", staffRepository.count());
        report.put("totalBoats", boatRepository.count());
        report.put("availableBoats", boatRepository.findByStatus("AVAILABLE").size());
        report.put("totalTrips", tripRepository.count());
        report.put("totalBookings", bookingRepository.count());

        double totalRevenue = activeBookings.stream().mapToDouble(Booking::getFinalPrice).sum();
        report.put("totalRevenue", totalRevenue);
    }
}
