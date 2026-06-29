package com.ibm.auth.controller;

import com.ibm.auth.common.payload.ApiResponse;
import com.ibm.auth.payload.request.LoginRequest;
import com.ibm.auth.payload.request.SignupRequest;
import com.ibm.auth.payload.response.LoginResponse;
import com.ibm.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request) {

        ApiResponse<Void> response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        ApiResponse<LoginResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}