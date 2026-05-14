package com.giftedlabs.prymageproduct.dto.response;

import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TicketDetailResponse(
        UUID id,
        String ticketNumber,
        String name,
        String email,
        String issueTitle,
        String description,
        TicketPriority priority,
        TicketStatus status,
        UUID assignedTo,
        UUID customerUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime resolvedAt,
        List<NoteResponse> notes
) {
}
