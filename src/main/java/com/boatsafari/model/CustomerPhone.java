package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "customer_phones")
@IdClass(CustomerPhone.Key.class)
public class CustomerPhone {

    @Id
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Id
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    public CustomerPhone() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Customer getCustomer() { return customer; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long customerId;
        private String phoneNumber;
        public Key() {}
        public Key(Long customerId, String phoneNumber) {
            this.customerId = customerId;
            this.phoneNumber = phoneNumber;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(customerId, k.customerId) && Objects.equals(phoneNumber, k.phoneNumber);
        }
        @Override public int hashCode() { return Objects.hash(customerId, phoneNumber); }
    }
}
