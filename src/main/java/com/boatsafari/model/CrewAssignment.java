package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/** Assigned-to relationship between Crew and Boat (many-to-many), with the assignment date. */
@Entity
@Table(name = "crew_assignments")
@IdClass(CrewAssignment.Key.class)
public class CrewAssignment {

    @Id
    @Column(name = "crew_id", nullable = false)
    private Long crewId;

    @Id
    @Column(name = "boat_id", nullable = false)
    private Long boatId;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate = LocalDate.now();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Crew crew;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boat_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Boat boat;

    public CrewAssignment() {}

    public Long getCrewId() { return crewId; }
    public void setCrewId(Long crewId) { this.crewId = crewId; }
    public Long getBoatId() { return boatId; }
    public void setBoatId(Long boatId) { this.boatId = boatId; }
    public LocalDate getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(LocalDate assignmentDate) { this.assignmentDate = assignmentDate; }
    public Crew getCrew() { return crew; }
    public Boat getBoat() { return boat; }

    /** Composite primary key (crewID, boatID). */
    public static class Key implements Serializable {
        private Long crewId;
        private Long boatId;
        public Key() {}
        public Key(Long crewId, Long boatId) { this.crewId = crewId; this.boatId = boatId; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(crewId, k.crewId) && Objects.equals(boatId, k.boatId);
        }
        @Override public int hashCode() { return Objects.hash(crewId, boatId); }
    }
}
