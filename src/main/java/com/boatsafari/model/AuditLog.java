package com.boatsafari.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Check(constraints = "action IN ('INSERT', 'UPDATE', 'DELETE')")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tableName;

    @Column(nullable = false, length = 50)
    private String recordId;

    @Column(nullable = false, length = 10)
    private String action; // INSERT, UPDATE, DELETE

    @Column(length = 500)
    private String oldValue;

    @Column(length = 500)
    private String newValue;

    @Column(nullable = false, length = 128)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(Long id, String tableName, String recordId, String action, String oldValue, String newValue, String changedBy, LocalDateTime changedAt) {
        this.id = id;
        this.tableName = tableName;
        this.recordId = recordId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}