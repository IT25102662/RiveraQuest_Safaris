package com.boatsafari.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Check;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "safety_checklist_items")
@IdClass(SafetyChecklistItem.Key.class)
@Check(constraints = "result IN ('Pass', 'Fail')")
public class SafetyChecklistItem {

    @Id
    @Column(name = "checklist_id", nullable = false)
    private Long checklistId;

    @Id
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "result", nullable = false, length = 4)
    private String result;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SafetyChecklist checklist;

    public SafetyChecklistItem() {}

    public Long getChecklistId() { return checklistId; }
    public void setChecklistId(Long checklistId) { this.checklistId = checklistId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public SafetyChecklist getChecklist() { return checklist; }

    /** Composite primary key. */
    public static class Key implements Serializable {
        private Long checklistId;
        private String itemName;
        public Key() {}
        public Key(Long checklistId, String itemName) {
            this.checklistId = checklistId;
            this.itemName = itemName;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return Objects.equals(checklistId, k.checklistId) && Objects.equals(itemName, k.itemName);
        }
        @Override public int hashCode() { return Objects.hash(checklistId, itemName); }
    }
}
