package com.boatsafari.config;

import com.boatsafari.model.*;
import com.boatsafari.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SafetyChecklistRepository safetyChecklistRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private BookingOfficerRepository bookingOfficerRepository;

    @Autowired
    private FleetManagerRepository fleetManagerRepository;

    @Autowired
    private SafetyOfficerRepository safetyOfficerRepository;

    @Autowired
    private MarketingOfficerRepository marketingOfficerRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TripRepository tripRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return; // Prevent duplicate initialization

        // 1. Seed Users (legacy table — still backs Admin's "User & Roles" page for now)
        User admin = userRepository.save(new User(null, "Vibuda senidu (Admin)", "admin@boatsafari.lk", "admin123", "+94 77 100 0001", "881234567V", "ADMIN", "ACTIVE"));
        User deskOfficer = userRepository.save(new User(null, "Jalina Waruna (Desk)", "desk@boatsafari.lk", "desk123", "+94 77 100 0002", "925678123V", "DESK_OFFICER", "ACTIVE"));
        User fleetManager = userRepository.save(new User(null, "chamikara liyanage", "fleet@boatsafari.lk", "fleet123", "+94 77 100 0003", "791238901V", "FLEET_MANAGER", "ACTIVE"));
        User safetyOfficer = userRepository.save(new User(null, "Uthsara ", "safety@boatsafari.lk", "safety123", "+94 77 100 0004", "853456789V", "SAFETY_OFFICER", "ACTIVE"));
        User marketingOfficer = userRepository.save(new User(null, "Chathuri Wickramasinghe", "marketing@boatsafari.lk", "marketing123", "+94 77 100 0005", "956789012V", "MARKETING_OFFICER", "ACTIVE"));
        User customer1 = userRepository.save(new User(null, "John Smith (Tourist)", "tourist@gmail.com", "tourist123", "+94 71 888 9999", "N-12345678", "CUSTOMER", "ACTIVE"));
        User customer2 = userRepository.save(new User(null, "Sarah Jenkins", "sarah@gmail.com", "sarah123", "+94 71 555 4444", "P-98765432", "CUSTOMER", "ACTIVE"));

        // 1b. Seed Staff (report-aligned tables) — same credentials as above
        Administrator adminStaff = new Administrator();
        adminStaff.setName("Vibuda senidu (Admin)");
        adminStaff.setEmail("admin@boatsafari.lk");
        adminStaff.setPasswordHash("admin123");
        adminStaff.setHireDate(LocalDate.now().minusYears(2));
        adminStaff.setAccessLevel("FULL");
        administratorRepository.save(adminStaff);

        BookingOfficer deskStaff = new BookingOfficer();
        deskStaff.setName("Jalina Waruna (Desk)");
        deskStaff.setEmail("desk@boatsafari.lk");
        deskStaff.setPasswordHash("desk123");
        deskStaff.setHireDate(LocalDate.now().minusYears(1));
        deskStaff.setDeskNumber("D-01");
        bookingOfficerRepository.save(deskStaff);

        FleetManager fleetStaff = new FleetManager();
        fleetStaff.setName("chamikara liyanage");
        fleetStaff.setEmail("fleet@boatsafari.lk");
        fleetStaff.setPasswordHash("fleet123");
        fleetStaff.setHireDate(LocalDate.now().minusYears(3));
        fleetStaff.setCertificationNumber("FM-CERT-1001");
        fleetManagerRepository.save(fleetStaff);

        SafetyOfficer safetyStaff = new SafetyOfficer();
        safetyStaff.setName("Uthsara");
        safetyStaff.setEmail("safety@boatsafari.lk");
        safetyStaff.setPasswordHash("safety123");
        safetyStaff.setHireDate(LocalDate.now().minusYears(1));
        safetyStaff.setSafetyLicenseNo("SL-2001");
        safetyOfficerRepository.save(safetyStaff);

        MarketingOfficer marketingStaff = new MarketingOfficer();
        marketingStaff.setName("Chathuri Wickramasinghe");
        marketingStaff.setEmail("marketing@boatsafari.lk");
        marketingStaff.setPasswordHash("marketing123");
        marketingStaff.setHireDate(LocalDate.now().minusMonths(8));
        marketingStaff.setDepartment("Marketing");
        marketingOfficerRepository.save(marketingStaff);

        // 1c. Seed Customers (report-aligned table)
        Customer customer1New = new Customer();
        customer1New.setFirstName("John");
        customer1New.setLastName("Smith (Tourist)");
        customer1New.setEmail("tourist@gmail.com");
        customer1New.setPasswordHash("tourist123");
        customer1New.setPhoneNumber("+94 71 888 9999");
        customer1New.setNicOrPassport("N-12345678");
        customerRepository.save(customer1New);

        Customer customer2New = new Customer();
        customer2New.setFirstName("Sarah");
        customer2New.setLastName("Jenkins");
        customer2New.setEmail("sarah@gmail.com");
        customer2New.setPasswordHash("sarah123");
        customer2New.setPhoneNumber("+94 71 555 4444");
        customer2New.setNicOrPassport("P-98765432");
        customerRepository.save(customer2New);

        // 2. Seed Boats
        Boat boat1 = new Boat(null, "Madu Lagoon Queen", "BS-LK-101", 20, "AVAILABLE", "GOOD", 100.0, "Captain Ruwan", 2, LocalDate.now().minusDays(10));
        boat1.setEngineType("Twin Diesel Inboard");
        boat1.setSafetyStatus("Cleared");
        boat1.setFleetManager(fleetStaff);
        boat1 = boatRepository.save(boat1);

        Boat boat2 = new Boat(null, "Mirissa Ocean Explorer", "BS-LK-202", 30, "AVAILABLE", "GOOD", 95.0, "Captain Sunil", 4, LocalDate.now().minusDays(5));
        boat2.setEngineType("Twin Diesel Inboard");
        boat2.setSafetyStatus("Cleared");
        boat2.setFleetManager(fleetStaff);
        boat2 = boatRepository.save(boat2);

        Boat boat3 = new Boat(null, "Bentota River Breeze", "BS-LK-303", 16, "AVAILABLE", "GOOD", 85.0, "Captain Dinesh", 2, LocalDate.now().minusDays(15));
        boat3.setEngineType("Single Outboard");
        boat3.setSafetyStatus("Cleared");
        boat3.setFleetManager(fleetStaff);
        boat3 = boatRepository.save(boat3);

        Boat boat4 = new Boat(null, "Pigeon Reef Runner", "BS-LK-404", 12, "MAINTENANCE", "NEEDS_SERVICE", 40.0, "Captain Kamal", 2, LocalDate.now().minusDays(30));
        boat4.setEngineType("Single Outboard");
        boat4.setSafetyStatus("Not Cleared");
        boat4.setFleetManager(fleetStaff);
        boat4 = boatRepository.save(boat4);

        // 3. Seed Maintenance Log
        maintenanceLogRepository.save(new MaintenanceLog(null, boat4, LocalDate.now().minusDays(2), "Routine dual-outboard engine spark plug and impeller replacement", 45000.0, "Southern Marine Workshop", "IN_PROGRESS"));
        maintenanceLogRepository.save(new MaintenanceLog(null, boat1, LocalDate.now().minusDays(10), "Full hull inspection, life jacket cleaning and bilge pump check", 18000.0, "Galle Boat Yard", "COMPLETED"));

        // 4. Seed Routes
        Route route1 = routeRepository.save(new Route(null, "Balapitiya Pier", "Kothduwa Island", 8.5));
        Route route2 = routeRepository.save(new Route(null, "Mirissa Harbour", "Offshore Whale Zone", 45.0));
        Route route3 = routeRepository.save(new Route(null, "Bentota River Estuary", "Sunset Viewpoint", 12.0));

        // 4b. Seed Guides
        Guide guide1 = guideRepository.save(new Guide(null, "Guide Amila", LocalDate.now().minusYears(2)));
        Guide guide2 = guideRepository.save(new Guide(null, "Naturalist Sanjeewa", LocalDate.now().minusYears(4)));
        Guide guide3 = guideRepository.save(new Guide(null, "Guide Mahesh", LocalDate.now().minusYears(1)));

        // 5. Seed Trips
        Trip trip1 = new Trip();
        trip1.setTripDate(LocalDate.now().plusDays(1));
        trip1.setDepartureTime(LocalTime.of(9, 0));
        trip1.setArrivalTime(LocalTime.of(11, 30));
        trip1.setPrice(4500.0);
        trip1.setPassengerCapacity(boat1.getCapacity());
        trip1.setBookedSeats(4);
        trip1.setBoat(boat1);
        trip1.setRoute(route1);
        trip1.setGuide(guide1);
        trip1 = tripRepository.save(trip1);

        Trip trip2 = new Trip();
        trip2.setTripDate(LocalDate.now().plusDays(1));
        trip2.setDepartureTime(LocalTime.of(6, 30));
        trip2.setArrivalTime(LocalTime.of(10, 30));
        trip2.setPrice(12500.0);
        trip2.setPassengerCapacity(boat2.getCapacity());
        trip2.setBookedSeats(5);
        trip2.setBoat(boat2);
        trip2.setRoute(route2);
        trip2.setGuide(guide2);
        trip2 = tripRepository.save(trip2);

        Trip trip3 = new Trip();
        trip3.setTripDate(LocalDate.now().plusDays(2));
        trip3.setDepartureTime(LocalTime.of(16, 30));
        trip3.setArrivalTime(LocalTime.of(18, 30));
        trip3.setPrice(5500.0);
        trip3.setPassengerCapacity(boat3.getCapacity());
        trip3.setBookedSeats(0);
        trip3.setBoat(boat3);
        trip3.setRoute(route3);
        trip3.setGuide(guide3);
        trip3 = tripRepository.save(trip3);

        // 6. Seed Pre-departure Safety Checklists
        safetyChecklistRepository.save(new SafetyChecklist(
                null, trip1, true, 20, true, "+94 77 999 1111", "CLEAR", true, "Dhammika Jayawardena", LocalDateTime.now(), "All safety gear verified. Clear weather predicted."
        ));

        safetyChecklistRepository.save(new SafetyChecklist(
                null, trip2, true, 30, true, "+94 77 999 2222", "CLEAR", true, "Dhammika Jayawardena", LocalDateTime.now(), "Ocean safety radio & extra life rafts checked."
        ));

        // 7. Seed Promotions & Vouchers
        Promotion promo1 = new Promotion(null, "SAFARI15", "15% discount on all river & mangrove safari packages", 15.0, LocalDate.now().minusDays(5), LocalDate.now().plusDays(30), true, 12);
        promo1.setTitle("River & Mangrove Safari Discount");
        promo1.setUsageLimit(100);
        promo1.setCreatedBy(marketingStaff);
        promotionRepository.save(promo1);

        Promotion promo2 = new Promotion(null, "SUMMER20", "20% off special offer for ocean wildlife expeditions", 20.0, LocalDate.now().minusDays(2), LocalDate.now().plusDays(45), true, 8);
        promo2.setTitle("Ocean Wildlife Summer Special");
        promo2.setUsageLimit(50);
        promo2.setCreatedBy(marketingStaff);
        promotionRepository.save(promo2);

        Promotion promo3 = new Promotion(null, "WELCOME10", "10% welcome discount for first-time online tourists", 10.0, LocalDate.now().minusDays(10), LocalDate.now().plusDays(60), true, 25);
        promo3.setTitle("First-Time Tourist Welcome Offer");
        promo3.setUsageLimit(200);
        promo3.setCreatedBy(marketingStaff);
        promotionRepository.save(promo3);

        // 8. Seed Bookings
        Booking booking1 = bookingRepository.save(new Booking(
                null, "BK-2026-8801", customer1New, trip1, 2, "S-01, S-02", 9000.0, 1350.0, 7650.0, LocalDateTime.now().minusDays(1), "CONFIRMED", "ONLINE_CARD", "Vegetarian preference on Cinnamon Island"
        ));

        Booking booking2 = bookingRepository.save(new Booking(
                null, "BK-2026-8802", customer2New, trip1, 2, "S-03, S-04", 9000.0, 0.0, 9000.0, LocalDateTime.now().minusHours(5), "CONFIRMED", "CASH_COUNTER", "Walk-in desk booking"
        ));

        Booking booking3 = bookingRepository.save(new Booking(
                null, "BK-2026-8803", customer1New, trip2, 5, "S-01, S-02, S-03, S-04, S-05", 62500.0, 12500.0, 50000.0, LocalDateTime.now().minusHours(2), "CONFIRMED", "ONLINE_CARD", "Family booking for Whale Watching"
        ));

        // 9. Seed Reviews
        reviewRepository.save(new Review(null, customer1New, trip1, 5, "Absolute highlight of our trip to Sri Lanka! The mangrove tunnels and fish spa were unforgettable.", LocalDateTime.now().minusDays(2), true));
        reviewRepository.save(new Review(null, customer2New, trip1, 4, "Great experience on Madu River! Captain Ruwan was very courteous and knowledgeable.", LocalDateTime.now().minusDays(1), true));
        reviewRepository.save(new Review(null, customer1New, trip2, 5, "We saw two massive Blue Whales and dolphins! Unbelievable ocean safari experience.", LocalDateTime.now().minusDays(3), true));

        System.out.println(">>> Boat Safari Management System Data Initialization Complete! <<<");
    }
}