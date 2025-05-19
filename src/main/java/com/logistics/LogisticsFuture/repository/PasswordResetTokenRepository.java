package com.logistics.LogisticsFuture.repository;

import com.logistics.LogisticsFuture.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenAndExpiresAtAfterAndUsedFalse(String token, Instant now);
}
