package com.boatsafari.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "guides")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate joinDate;

    public Guide() {}

    public Guide(Long id, String name, LocalDate joinDate) {
        this.id = id;
        this.name = name;
        this.joinDate = joinDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
}