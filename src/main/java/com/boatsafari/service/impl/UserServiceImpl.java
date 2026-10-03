package com.boatsafari.service.impl;

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
            String status = "Active".equalsIgnoreCase(c.getAccountStatus()) ? "ACTIVE" : "SUSPENDED";
            return new LoginResponseDTO(c.getId(), c.getFullName(), c.getEmail(), "CUSTOMER", status, "token-" + c.getId());
        }

        Optional<Staff> staffOpt = staffRepository.findByEmail(email);
        if (staffOpt.isPresent()) {
            Staff s = staffOpt.get();
            if (!passwordMatches(s.getPasswordHash(), password)) {
                throw new BusinessRuleException("Invalid email or password!");
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
