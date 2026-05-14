package com.giftedlabs.prymageproduct.dto.request;

import com.giftedlabs.prymageproduct.enums.TicketPriority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PublicTicketRequest(
        @NotBlank @Size(min = 2, max = 150) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 5, max = 300) String issueTitle,
        @NotBlank @Size(min = 10, max = 5000) String description,
        @NotNull TicketPriority priority
) {
}
