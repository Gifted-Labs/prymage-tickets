package com.giftedlabs.prymageproduct.dto.response;

import com.giftedlabs.prymageproduct.enums.Role;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        Role role
) {
}
