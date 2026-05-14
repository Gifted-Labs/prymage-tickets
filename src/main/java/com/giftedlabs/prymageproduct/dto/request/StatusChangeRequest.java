package com.giftedlabs.prymageproduct.dto.request;

import com.giftedlabs.prymageproduct.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record StatusChangeRequest(@NotNull TicketStatus status) {
}
