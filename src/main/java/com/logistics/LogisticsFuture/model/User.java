package com.logistics.LogisticsFuture.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean termsAgreed;

    @Column(nullable = false)
    private boolean marketingAgreed;

    @Column(nullable = false)
    private boolean enabled = true;
}
