package com.giftedlabs.prymageproduct.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID ticketId,
        UUID changedBy,
        String changedByName,
        String action,
        String oldValue,
        String newValue,
        OffsetDateTime createdAt
) {
}
