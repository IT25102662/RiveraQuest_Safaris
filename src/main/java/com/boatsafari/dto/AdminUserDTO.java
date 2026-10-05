package com.boatsafari.dto;

/** One row of the Admin "User Accounts" page: a customer or a staff member, shown in one common shape. */
public class AdminUserDTO {
    private final String key;       // "C-<id>" for customers, "S-<id>" for staff (ids overlap between the two tables)
    private final String type;      // CUSTOMER or STAFF
    private final Long id;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String role;
    private final String status;    // ACTIVE or SUSPENDED

    public AdminUserDTO(String type, Long id, String fullName, String email, String phoneNumber, String role, String status) {
        this.key = ("CUSTOMER".equals(type) ? "C-" : "S-") + id;
        this.type = type;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber == null ? "" : phoneNumber;
        this.role = role;
        this.status = status;
    }

    public String getKey() { return key; }
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}
