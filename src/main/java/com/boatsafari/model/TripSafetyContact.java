package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "trip_safety_contacts")
@IdClass(TripSafetyContact.Key.class)
public class TripSafetyContact {

    @Id
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Id
    @Column(name = "contact_id", nullable = false)
    private Integer contactId;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "contact_type", length = 30)
    private String contactType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Trip trip;

    public TripSafetyContact() {}

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Integer getContactId() { return contactId; }
    public void setContactId(Integer contactId) { this.contactId = contactId; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }
    public Trip getTrip() { return trip; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long tripId;
        private Integer contactId;
        public Key() {}
        public Key(Long tripId, Integer contactId) {
            this.tripId = tripId;
            this.contactId = contactId;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(tripId, k.tripId) && Objects.equals(contactId, k.contactId);
        }
        @Override public int hashCode() { return Objects.hash(tripId, contactId); }
    }
}
