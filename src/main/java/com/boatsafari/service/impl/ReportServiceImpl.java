package com.boatsafari.service.impl;

import com.boatsafari.model.Booking;
import com.boatsafari.repository.*;
import com.boatsafari.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Map<String, Object> getSystemSummaryReport() {
        Map<String, Object> report = new HashMap<>();

        report.put("totalCustomers", customerRepository.count());
        report.put("totalStaff", staffRepository.count());
        report.put("totalBoats", boatRepository.count());
        report.put("availableBoats", boatRepository.findByStatus("AVAILABLE").size());
        report.put("totalTrips", tripRepository.count());
        report.put("totalBookings", bookingRepository.count());

        double totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .mapToDouble(Booking::getFinalPrice)
                .sum();
        report.put("totalRevenue", totalRevenue);

        return report;
    }

    @Override
    public Map<String, Object> getRevenueReport() {
        Map<String, Object> report = new HashMap<>();

        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .toList();

        double grossRevenue = activeBookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        double totalDiscounts = activeBookings.stream().mapToDouble(Booking::getDiscountAmount).sum();
        double netRevenue = activeBookings.stream().mapToDouble(Booking::getFinalPrice).sum();

        report.put("grossRevenue", grossRevenue);
        report.put("totalDiscounts", totalDiscounts);
        report.put("netRevenue", netRevenue);
        report.put("totalBookingsCount", activeBookings.size());

        return report;
    }

    @Override
    public Map<String, Object> getBoatUtilizationReport() {
        Map<String, Object> report = new HashMap<>();

        report.put("availableBoatsCount", boatRepository.findByStatus("AVAILABLE").size());
        report.put("maintenanceBoatsCount", boatRepository.findByStatus("MAINTENANCE").size());
        report.put("inUseBoatsCount", boatRepository.findByStatus("IN_USE").size());

        return report;
    }
}
