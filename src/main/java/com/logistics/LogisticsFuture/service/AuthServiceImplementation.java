package com.logistics.LogisticsFuture.service;


import com.logistics.LogisticsFuture.dto.*;
import com.logistics.LogisticsFuture.model.PasswordResetToken;
import com.logistics.LogisticsFuture.model.RefreshToken;
import com.logistics.LogisticsFuture.model.User;
import com.logistics.LogisticsFuture.projection.UserAuthProjection;
import com.logistics.LogisticsFuture.projection.UserMinimalProjection;
import com.logistics.LogisticsFuture.projection.UserRefreshProjection;
import com.logistics.LogisticsFuture.repository.PasswordResetTokenRepository;
import com.logistics.LogisticsFuture.repository.RefreshTokenRepository;
import com.logistics.LogisticsFuture.repository.UserRepository;
import com.logistics.LogisticsFuture.utility.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImplementation {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    public UserMinimalProjection register(RegisterRequest request) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            Optional<UserAuthProjection> existingUserOpt = userRepository.findByEmail(request.getEmail(), UserAuthProjection.class);
            if (existingUserOpt.isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }

            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setTermsAgreed(request.isTermsAgreed());
            user.setMarketingAgreed(request.isMarketingAgreed());

            userRepository.save(user);
            System.out.println("New user registered: " + user.getEmail());

            return userRepository.findByUserId(user.getUserId(), UserMinimalProjection.class)
                    .orElseThrow(() -> new IllegalStateException("User not found after save"));
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            throw new RuntimeException("Registration failed", e);
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Optional<UserAuthProjection> userOpt = userRepository.findByEmail(request.getEmail(), UserAuthProjection.class);
            if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
                throw new IllegalArgumentException("Invalid credentials");
            }

            UserAuthProjection user = userOpt.get();
            String jwt = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

            RefreshToken tokenEntity = new RefreshToken();
            tokenEntity.setUserId(user.getUserId());
            tokenEntity.setToken(refreshToken);
            tokenEntity.setExpiresAt(Instant.now().plusMillis(604800000));
            refreshTokenRepository.save(tokenEntity);
            AuthResponse response = new AuthResponse();
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            return response;
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        try {
            RefreshToken token = refreshTokenRepository.findByTokenAndExpiresAtAfter(request.getRefreshToken(), Instant.now())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));

            UserRefreshProjection user = userRepository.findByRefreshToken(request.getRefreshToken(), Instant.now(), UserRefreshProjection.class)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));

            String newJwt = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

            token.setToken(newRefreshToken);
            token.setExpiresAt(Instant.now().plusMillis(604800000));
            refreshTokenRepository.save(token);
            AuthResponse response = new AuthResponse();
            response.setToken(newJwt);
            response.setRefreshToken(newRefreshToken);
            return response;
        } catch (Exception e) {
            System.err.println("Error during token refresh: " + e.getMessage());
            throw new RuntimeException("Token refresh failed", e);
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        try {
            Optional<UserMinimalProjection> userOpt = userRepository.findByEmail(request.getEmail(), UserMinimalProjection.class);
            if (userOpt.isEmpty()) {
                System.out.println("No user found with email: " + request.getEmail());
                return;
            }

            UserMinimalProjection user = userOpt.get();
            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUserId(user.getUserId());
            resetToken.setToken(token);
            resetToken.setExpiresAt(Instant.now().plusMillis(3600000));
            passwordResetTokenRepository.save(resetToken);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n" +
                    "http://localhost:8080/api/auth/reset-password?token=" + token);

            mailSender.send(message);
            System.out.println("Reset email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error during forgot password process: " + e.getMessage());
            throw new RuntimeException("Forgot password failed", e);
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            PasswordResetToken token = passwordResetTokenRepository
                    .findByTokenAndExpiresAtAfterAndUsedFalse(request.getToken(), Instant.now())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

            UserMinimalProjection user = userRepository.findByResetToken(request.getToken(), Instant.now(), UserMinimalProjection.class)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for reset token"));

            User userEntity = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(userEntity);

            token.setUsed(true);
            passwordResetTokenRepository.save(token);
        } catch (Exception e) {
            System.err.println("Error during password reset: " + e.getMessage());
            throw new RuntimeException("Password reset failed", e);
        }
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        try {
            Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenAndExpiresAtAfter(request.getRefreshToken(), Instant.now());

            if (tokenOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }

            System.out.println("Token found, proceeding with deletion: " + request.getRefreshToken());
            refreshTokenRepository.deleteByToken(request.getRefreshToken());
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            throw new RuntimeException("Logout failed", e);
        }
    }
}