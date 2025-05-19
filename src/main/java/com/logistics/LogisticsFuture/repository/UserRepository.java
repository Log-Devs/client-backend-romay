package com.logistics.LogisticsFuture.repository;

import com.logistics.LogisticsFuture.model.User;
import com.logistics.LogisticsFuture.projection.UserAuthProjection;
import com.logistics.LogisticsFuture.projection.UserMinimalProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    <T> Optional<T> findByEmail(String email, Class<T> type);
    @Query("SELECT u FROM User u JOIN RefreshToken rt ON u.userId = rt.userId WHERE rt.token = :token AND rt.expiresAt > :now")
    <T> Optional<T> findByRefreshToken(String token, Instant now, Class<T> type);

    @Query("SELECT u FROM User u JOIN PasswordResetToken prt ON u.userId = prt.userId WHERE prt.token = :token AND prt.expiresAt > :now AND prt.used = false")
    <T> Optional<T> findByResetToken(String token, Instant now, Class<T> type);
    <T> Optional<T> findByUserId(UUID userId, Class<T> type);


}