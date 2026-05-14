package com.giftedlabs.prymageproduct.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserActivationRequest(@NotNull Boolean isActive) {
}
