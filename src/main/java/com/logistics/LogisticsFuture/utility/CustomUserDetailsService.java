package com.logistics.LogisticsFuture.utility;

import com.logistics.LogisticsFuture.projection.UserAuthProjection;
import com.logistics.LogisticsFuture.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UUID uuid = UUID.fromString(userId);

        UserAuthProjection user = userRepository.findByUserId(uuid, UserAuthProjection.class)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return org.springframework.security.core.userdetails.User.withUsername(user.getUserId().toString())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}