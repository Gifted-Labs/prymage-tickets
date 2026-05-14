package com.giftedlabs.prymageproduct.dto.response;

import com.giftedlabs.prymageproduct.enums.NoteType;
import java.time.OffsetDateTime;

public record PublicTicketReplyResponse(
        String content,
        OffsetDateTime createdAt,
        NoteType noteType
) {
}
