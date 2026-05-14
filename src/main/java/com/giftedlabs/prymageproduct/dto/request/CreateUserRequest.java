package com.giftedlabs.prymageproduct.dto.request;

import com.giftedlabs.prymageproduct.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(min = 2, max = 150) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 128)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "must contain at least 1 uppercase letter and 1 digit")
        String password,
        @NotNull Role role
) {
}
