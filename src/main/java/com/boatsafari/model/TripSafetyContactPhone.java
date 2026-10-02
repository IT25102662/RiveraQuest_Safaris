package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "trip_safety_contact_phones")
@IdClass(TripSafetyContactPhone.Key.class)
public class TripSafetyContactPhone {

    @Id
    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Id
    @Column(name = "contact_id", nullable = false)
    private Integer contactId;

    @Id
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "trip_id", referencedColumnName = "trip_id", insertable = false, updatable = false),
        @JoinColumn(name = "contact_id", referencedColumnName = "contact_id", insertable = false, updatable = false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TripSafetyContact contact;

    public TripSafetyContactPhone() {}

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public Integer getContactId() { return contactId; }
    public void setContactId(Integer contactId) { this.contactId = contactId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public TripSafetyContact getContact() { return contact; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long tripId;
        private Integer contactId;
        private String phoneNumber;
        public Key() {}
        public Key(Long tripId, Integer contactId, String phoneNumber) {
            this.tripId = tripId;
            this.contactId = contactId;
            this.phoneNumber = phoneNumber;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(tripId, k.tripId) && Objects.equals(contactId, k.contactId) && Objects.equals(phoneNumber, k.phoneNumber);
        }
        @Override public int hashCode() { return Objects.hash(tripId, contactId, phoneNumber); }
    }
}
