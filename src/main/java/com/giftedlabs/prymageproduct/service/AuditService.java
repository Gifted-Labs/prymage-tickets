package com.giftedlabs.prymageproduct.service;

import com.giftedlabs.prymageproduct.dto.response.AuditLogResponse;
import com.giftedlabs.prymageproduct.entity.Ticket;
import com.giftedlabs.prymageproduct.exception.ResourceNotFoundException;
import com.giftedlabs.prymageproduct.repository.AuditLogRepository;
import com.giftedlabs.prymageproduct.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditService {
    private final TicketRepository ticketRepository;
    private final AuditLogRepository auditLogRepository;

    public AuditService(TicketRepository ticketRepository, AuditLogRepository auditLogRepository) {
        this.ticketRepository = ticketRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return auditLogRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
                .map(a -> new AuditLogResponse(
                        a.getId(),
                        a.getTicket().getId(),
                        a.getChangedBy().getId(),
                        a.getChangedBy().getFullName(),
                        a.getAction(),
                        a.getOldValue(),
                        a.getNewValue(),
                        a.getCreatedAt()))
                .toList();
    }
}
