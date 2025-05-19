package com.logistics.LogisticsFuture.config;

import com.logistics.LogisticsFuture.utility.CustomLogoutSuccessHandler;
import com.logistics.LogisticsFuture.utility.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/logisticsFuture/auth/register","/api/logisticsFuture/auth/login","/api/logisticsFuture/auth/refresh",
                                "/api/logisticsFuture/auth/reset-password","/api/logisticsFuture/auth/forgot-password","/api/logisticsFuture/auth/logout")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/logisticsFuture/auth/register","/api/logisticsFuture/auth/login","/api/logisticsFuture/auth/refresh",
                                "/api/logisticsFuture/auth/reset-password","/api/logisticsFuture/auth/forgot-password","/api/logisticsFuture/auth/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form-> form
                        .loginPage("/login").disable()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/parent/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
