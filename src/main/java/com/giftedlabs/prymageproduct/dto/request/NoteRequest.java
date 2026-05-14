package com.giftedlabs.prymageproduct.dto.request;

import com.giftedlabs.prymageproduct.enums.NoteType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank @Size(min = 1, max = 5000) String content,
        @NotNull NoteType noteType
) {
}
