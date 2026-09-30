package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "staff_type", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDate hireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Staff supervisor;

    public Staff() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Staff getSupervisor() { return supervisor; }
    public void setSupervisor(Staff supervisor) { this.supervisor = supervisor; }
}