package com.logistics.LogisticsFuture.controller;

import com.logistics.LogisticsFuture.dto.*;
import com.logistics.LogisticsFuture.projection.UserMinimalProjection;
import com.logistics.LogisticsFuture.service.AuthServiceImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logisticsFuture/auth")
public class AuthController {
    private final AuthServiceImplementation authService;


    @PostMapping("/register")
    public ResponseEntity<UserMinimalProjection> register(@Valid @RequestBody RegisterRequest request) {
        UserMinimalProjection user = authService.register(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new MessageResponse("If the email exists, a reset link has been sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}