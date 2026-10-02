package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "emergency_contacts")
@IdClass(EmergencyContact.Key.class)
public class EmergencyContact {

    @Id
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Id
    @Column(name = "contact_id", nullable = false)
    private Integer contactId;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "relationship", length = 50)
    private String relationship;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    public EmergencyContact() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Integer getContactId() { return contactId; }
    public void setContactId(Integer contactId) { this.contactId = contactId; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public Customer getCustomer() { return customer; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long customerId;
        private Integer contactId;
        public Key() {}
        public Key(Long customerId, Integer contactId) {
            this.customerId = customerId;
            this.contactId = contactId;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(customerId, k.customerId) && Objects.equals(contactId, k.contactId);
        }
        @Override public int hashCode() { return Objects.hash(customerId, contactId); }
    }
}
