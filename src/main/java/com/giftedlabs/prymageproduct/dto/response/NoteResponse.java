package com.giftedlabs.prymageproduct.dto.response;

import com.giftedlabs.prymageproduct.enums.NoteType;
import java.time.OffsetDateTime;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        String content,
        NoteType noteType,
        UUID authorId,
        String authorName,
        OffsetDateTime createdAt
) {
}
