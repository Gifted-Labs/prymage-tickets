package com.giftedlabs.prymageproduct.dto.response;

import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketSummaryResponse(
        UUID id,
        String ticketNumber,
        String issueTitle,
        String name,
        String email,
        TicketPriority priority,
        TicketStatus status,
        UUID assignedTo,
        OffsetDateTime createdAt
) {
}
