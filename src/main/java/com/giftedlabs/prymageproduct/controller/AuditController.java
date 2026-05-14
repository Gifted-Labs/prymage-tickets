package com.giftedlabs.prymageproduct.controller;

import com.giftedlabs.prymageproduct.dto.response.AuditLogResponse;
import com.giftedlabs.prymageproduct.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<List<AuditLogResponse>> byTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(auditService.getByTicket(ticketId));
    }
}
