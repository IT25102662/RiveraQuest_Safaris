package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "emergency_contact_phones")
@IdClass(EmergencyContactPhone.Key.class)
public class EmergencyContactPhone {

    @Id
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Id
    @Column(name = "contact_id", nullable = false)
    private Integer contactId;

    @Id
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", insertable = false, updatable = false),
        @JoinColumn(name = "contact_id", referencedColumnName = "contact_id", insertable = false, updatable = false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EmergencyContact contact;

    public EmergencyContactPhone() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Integer getContactId() { return contactId; }
    public void setContactId(Integer contactId) { this.contactId = contactId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public EmergencyContact getContact() { return contact; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long customerId;
        private Integer contactId;
        private String phoneNumber;
        public Key() {}
        public Key(Long customerId, Integer contactId, String phoneNumber) {
            this.customerId = customerId;
            this.contactId = contactId;
            this.phoneNumber = phoneNumber;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(customerId, k.customerId) && Objects.equals(contactId, k.contactId) && Objects.equals(phoneNumber, k.phoneNumber);
        }
        @Override public int hashCode() { return Objects.hash(customerId, contactId, phoneNumber); }
    }
}
