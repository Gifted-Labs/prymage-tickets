package com.giftedlabs.prymageproduct.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignTicketRequest(@NotNull UUID staffUserId) {
}
