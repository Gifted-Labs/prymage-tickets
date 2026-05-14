package com.giftedlabs.prymageproduct.repository;

import com.giftedlabs.prymageproduct.entity.AuditLog;
import com.giftedlabs.prymageproduct.entity.Ticket;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}
