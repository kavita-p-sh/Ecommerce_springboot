package com.ecommerce.api.config;

import com.ecommerce.api.security.JwtAuthenticationFilter;
import com.ecommerce.api.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Configures security for the application.
 * Handles JWT, authentication, and API access rules.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Provides password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Removes ROLE_ prefix from authorities.
     */
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    /**
     * Provides authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines security rules and JWT filter.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/name/*").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/products").hasAuthority(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/products").hasAuthority(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAuthority(AppConstants.ROLE_ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAuthority(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority(AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority(AppConstants.ROLE_ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/orders").hasAnyAuthority(AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/orders/cancel/*").authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}