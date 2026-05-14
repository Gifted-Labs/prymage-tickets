package com.giftedlabs.prymageproduct.service;

import com.giftedlabs.prymageproduct.dto.request.LoginRequest;
import com.giftedlabs.prymageproduct.dto.request.RegisterRequest;
import com.giftedlabs.prymageproduct.dto.response.AuthResponse;
import com.giftedlabs.prymageproduct.dto.response.UserResponse;
import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.exception.ConflictException;
import com.giftedlabs.prymageproduct.exception.ForbiddenOperationException;
import com.giftedlabs.prymageproduct.repository.UserRepository;
import com.giftedlabs.prymageproduct.security.AppUserDetails;
import com.giftedlabs.prymageproduct.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.isActive()) {
            throw new ForbiddenOperationException("Account is inactive");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(principal);

            return new AuthResponse(
                    token,
                    "Bearer",
                    jwtTokenProvider.getExpirySeconds(),
                    new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole())
            );
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
