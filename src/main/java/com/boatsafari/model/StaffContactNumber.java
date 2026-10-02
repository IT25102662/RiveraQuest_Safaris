package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "staff_contact_numbers")
@IdClass(StaffContactNumber.Key.class)
public class StaffContactNumber {

    @Id
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @Id
    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Staff staff;

    public StaffContactNumber() {}

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Staff getStaff() { return staff; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long staffId;
        private String contactNumber;
        public Key() {}
        public Key(Long staffId, String contactNumber) {
            this.staffId = staffId;
            this.contactNumber = contactNumber;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(staffId, k.staffId) && Objects.equals(contactNumber, k.contactNumber);
        }
        @Override public int hashCode() { return Objects.hash(staffId, contactNumber); }
    }
}
