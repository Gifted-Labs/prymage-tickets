package com.giftedlabs.prymageproduct.controller;

import com.giftedlabs.prymageproduct.dto.request.CreateUserRequest;
import com.giftedlabs.prymageproduct.dto.request.UpdateUserActivationRequest;
import com.giftedlabs.prymageproduct.dto.response.PagedResponse;
import com.giftedlabs.prymageproduct.dto.response.UserResponse;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> list(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(userService.list(role, page, size));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activate(@PathVariable UUID id, @Valid @RequestBody UpdateUserActivationRequest request) {
        return ResponseEntity.ok(userService.updateActivation(id, request.isActive()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.softDelete(id));
    }
}
