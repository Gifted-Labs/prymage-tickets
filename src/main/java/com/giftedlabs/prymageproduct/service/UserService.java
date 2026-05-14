package com.giftedlabs.prymageproduct.service;

import com.giftedlabs.prymageproduct.dto.request.CreateUserRequest;
import com.giftedlabs.prymageproduct.dto.response.PagedResponse;
import com.giftedlabs.prymageproduct.dto.response.UserResponse;
import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.exception.ConflictException;
import com.giftedlabs.prymageproduct.exception.ForbiddenOperationException;
import com.giftedlabs.prymageproduct.exception.ResourceNotFoundException;
import com.giftedlabs.prymageproduct.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> list(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> result = role == null ? userRepository.findAll(pageable) : userRepository.findByRole(role, pageable);
        return new PagedResponse<>(
                result.map(this::toResponse).getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (request.role() == Role.CUSTOMER) {
            throw new ForbiddenOperationException("Use /auth/register for customer account creation");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(true);

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateActivation(UUID id, boolean active) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(active);
        return toResponse(user);
    }

    @Transactional
    public UserResponse softDelete(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
