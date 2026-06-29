package com.ibm.auth.service.impl;

import com.ibm.auth.common.exception.EmailAlreadyExistsException;
import com.ibm.auth.common.exception.UsernameAlreadyExistsException;
import com.ibm.auth.common.exception.InvalidCredentialsException;
import com.ibm.auth.common.payload.ApiResponse;
import com.ibm.auth.entity.User;
import com.ibm.auth.payload.enums.Role;
import com.ibm.auth.payload.request.LoginRequest;
import com.ibm.auth.payload.request.SignupRequest;
import com.ibm.auth.payload.response.LoginResponse;
import com.ibm.auth.repository.UserRepository;
import com.ibm.auth.common.security.JwtUtil;
import com.ibm.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public ApiResponse<Void> signup(SignupRequest request) {

        // Check username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // Check email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Check passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        // Build user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.ROLE_EMPLOYEE))
                .enabled(true)
                .accountLocked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new ApiResponse<>(true, "User registered successfully", null);
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Get roles
            Set<Role> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());

            // Generate JWT
            String token = jwtUtil.generateToken(request.getUsername());

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .username(request.getUsername())
                    .roles(roles)
                    .build();

            return new ApiResponse<>(true, "Login successful", loginResponse);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
}