package com.giftedlabs.prymageproduct.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "old_value", length = 255)
    private String oldValue;

    @Column(name = "new_value", length = 255)
    private String newValue;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public User getChangedBy() { return changedBy; }
    public void setChangedBy(User changedBy) { this.changedBy = changedBy; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
