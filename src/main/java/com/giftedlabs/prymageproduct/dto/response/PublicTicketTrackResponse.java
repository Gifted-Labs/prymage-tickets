package com.giftedlabs.prymageproduct.dto.response;

import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import java.time.OffsetDateTime;
import java.util.List;

public record PublicTicketTrackResponse(
        String ticketNumber,
        String issueTitle,
        TicketStatus status,
        TicketPriority priority,
        OffsetDateTime createdAt,
        List<PublicTicketReplyResponse> replies
) {
}
