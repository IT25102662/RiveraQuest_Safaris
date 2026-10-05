package com.boatsafari.service.impl;

import com.boatsafari.dto.AdminUserDTO;
import com.boatsafari.dto.LoginRequestDTO;
import com.boatsafari.dto.LoginResponseDTO;
import com.boatsafari.exception.BusinessRuleException;
import com.boatsafari.exception.ResourceNotFoundException;
import com.boatsafari.model.Administrator;
import com.boatsafari.model.BookingOfficer;
import com.boatsafari.model.Customer;
import com.boatsafari.model.FleetManager;
import com.boatsafari.model.MarketingOfficer;
import com.boatsafari.model.SafetyOfficer;
import com.boatsafari.model.Staff;
import com.boatsafari.model.User;
import com.boatsafari.repository.CustomerRepository;
import com.boatsafari.repository.StaffRepository;
import com.boatsafari.repository.UserRepository;
import com.boatsafari.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository; // legacy table — still backs the Admin "User & Roles" page for now

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public User registerUser(User user) {
        String email = user.getEmail() == null ? "" : user.getEmail().trim();
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")) {
            throw new BusinessRuleException("Please enter a valid email address.");
        }
        user.setEmail(email);
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new BusinessRuleException("Full name is required.");
        }
        if (user.getFullName().trim().length() > 100 || user.getFullName().trim().split(" ", 2)[0].length() > 50) {
            throw new BusinessRuleException("Full name is too long.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new BusinessRuleException("Password must be at least 6 characters long.");
        }
        if (customerRepository.existsByEmail(user.getEmail())) {
            throw new BusinessRuleException("An account with this email already exists: " + user.getEmail());
        }

        String fullName = user.getFullName() != null ? user.getFullName().trim() : "Customer";
        String[] parts = fullName.split(" ", 2);

        Customer customer = new Customer();
        customer.setFirstName(parts[0]);
        customer.setLastName(parts.length > 1 ? parts[1] : "");
        customer.setEmail(user.getEmail());
        customer.setPasswordHash(user.getPassword());
        customer.setPhoneNumber(user.getPhoneNumber());
        customer.setNicOrPassport(user.getNicOrPassport());
        Customer saved = customerRepository.save(customer);

        // Return a transient User-shaped response for API compatibility — not persisted to the legacy users table
        return new User(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getPasswordHash(), saved.getPhoneNumber(), saved.getNicOrPassport(), "CUSTOMER", "ACTIVE");
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        // Block suspended accounts (status is managed from the Admin "User & Roles" page)
        Optional<User> accountOpt = userRepository.findByEmail(email);
        if (accountOpt.isPresent() && "SUSPENDED".equalsIgnoreCase(accountOpt.get().getStatus())) {
            throw new BusinessRuleException("Your account has been suspended. Please contact the administrator.");
        }

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer c = customerOpt.get();
            if (!passwordMatches(c.getPasswordHash(), password)) {
                throw new BusinessRuleException("Invalid email or password!");
            }
            if (!"Active".equalsIgnoreCase(c.getAccountStatus())) {
                throw new BusinessRuleException("Your account has been suspended. Please contact the administrator.");
            }
            return new LoginResponseDTO(c.getId(), c.getFullName(), c.getEmail(), "CUSTOMER", "ACTIVE", "token-" + c.getId());
        }

        Optional<Staff> staffOpt = staffRepository.findByEmail(email);
        if (staffOpt.isPresent()) {
            Staff s = staffOpt.get();
            if (!passwordMatches(s.getPasswordHash(), password)) {
                throw new BusinessRuleException("Invalid email or password!");
            }
            if (!"Active".equalsIgnoreCase(s.getAccountStatus())) {
                throw new BusinessRuleException("Your account has been suspended. Please contact the administrator.");
            }
            return new LoginResponseDTO(s.getId(), s.getName(), s.getEmail(), mapStaffRole(s), "ACTIVE", "token-" + s.getId());
        }

        throw new ResourceNotFoundException("No account found with email: " + email);
    }

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /** Accepts a bcrypt hash ($2a$/$2b$/$2y$) or, for the older demo accounts, a plain stored password. */
    private boolean passwordMatches(String stored, String entered) {
        if (stored == null || entered == null) return false;
        if (stored.startsWith("$2")) return PASSWORD_ENCODER.matches(entered, stored);
        return stored.equals(entered);
    }

    private String mapStaffRole(Staff s) {
        if (s instanceof BookingOfficer) return "DESK_OFFICER";
        if (s instanceof FleetManager) return "FLEET_MANAGER";
        if (s instanceof SafetyOfficer) return "SAFETY_OFFICER";
        if (s instanceof MarketingOfficer) return "MARKETING_OFFICER";
        if (s instanceof Administrator) return "ADMIN";
        return "STAFF";
    }

    /** Every customer and staff member, in one list for the Admin "User Accounts" page. */
    @Override
    public List<AdminUserDTO> getAllAccounts() {
        List<AdminUserDTO> accounts = new ArrayList<>();

        List<Staff> staff = new ArrayList<>(staffRepository.findAll());
        staff.sort(Comparator.comparing(Staff::getId));
        for (Staff s : staff) {
            // Demo staff still keep their phone number in the older users table
            String phone = userRepository.findByEmail(s.getEmail()).map(User::getPhoneNumber).orElse("");
            String status = "Active".equalsIgnoreCase(s.getAccountStatus()) ? "ACTIVE" : "SUSPENDED";
            accounts.add(new AdminUserDTO("STAFF", s.getId(), s.getName(), s.getEmail(), phone, mapStaffRole(s), status));
        }

        List<Customer> customers = new ArrayList<>(customerRepository.findAll());
        customers.sort(Comparator.comparing(Customer::getId));
        for (Customer c : customers) {
            String status = "Active".equalsIgnoreCase(c.getAccountStatus()) ? "ACTIVE" : "SUSPENDED";
            accounts.add(new AdminUserDTO("CUSTOMER", c.getId(), c.getFullName(), c.getEmail(), c.getPhoneNumber(), "CUSTOMER", status));
        }
        return accounts;
    }

    /** Suspend or activate one account. The key is "C-<id>" (customer) or "S-<id>" (staff). */
    @Override
    public AdminUserDTO updateAccountStatus(String key, String status) {
        String wanted = status == null ? "" : status.trim().toUpperCase();
        if (!wanted.equals("ACTIVE") && !wanted.equals("SUSPENDED")) {
            throw new BusinessRuleException("Status must be ACTIVE or SUSPENDED.");
        }
        if (key == null || !key.matches("[CS]-\\d+")) {
            throw new BusinessRuleException("Invalid account reference: " + key);
        }
        Long id = Long.valueOf(key.substring(2));
        boolean active = wanted.equals("ACTIVE");

        String email;
        AdminUserDTO result;
        if (key.startsWith("C-")) {
            Customer c = customerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
            c.setAccountStatus(active ? "Active" : "Inactive");   // the database only allows Active or Inactive here
            customerRepository.save(c);
            email = c.getEmail();
            result = new AdminUserDTO("CUSTOMER", c.getId(), c.getFullName(), c.getEmail(), c.getPhoneNumber(), "CUSTOMER", wanted);
        } else {
            Staff s = staffRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + id));
            s.setAccountStatus(active ? "Active" : "Suspended");
            staffRepository.save(s);
            email = s.getEmail();
            result = new AdminUserDTO("STAFF", s.getId(), s.getName(), s.getEmail(), "", mapStaffRole(s), wanted);
        }

        // Keep the older users table in step, because the login check also reads it
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setStatus(wanted);
            userRepository.save(u);
        });
        return result;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existing = getUserById(id);
        existing.setFullName(updatedUser.getFullName());
        existing.setPhoneNumber(updatedUser.getPhoneNumber());
        existing.setStatus(updatedUser.getStatus());
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(getUserById(id));
    }
    @Override
    public User updateUserStatus(Long id, String status) {
        User user = getUserById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }
}
