package com.si516.saludconecta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()  // Allow authentication endpoints
                        .requestMatchers("/doctors/**").permitAll()  // Allow doctor CRUD for now (to be secured later)
                        .requestMatchers("/offices/**").permitAll()  // Allow office endpoints for now
                        .requestMatchers("/patients/**").permitAll()  // Allow patient endpoints for now
                        .requestMatchers("/appointments/**").permitAll()  // Allow appointment endpoints for now
                        .requestMatchers("/clinic-histories/**").permitAll()  // Allow clinic history endpoints for now
                        .requestMatchers("/actuator/**").permitAll()  // Allow actuator endpoints
                        .anyRequest().permitAll()  // For now, allow all other requests
                );

        return http.build();
    }
}