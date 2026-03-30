package Sb_new_project.demo.security;

import Sb_new_project.demo.util.Constant;
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

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true)
public class SpringSecurity {

    private final JwtFilter jwtFilter;
//    private final SessionRegistry sessionRegistry;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



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

                        .requestMatchers(HttpMethod.POST, "/api/products").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/products/*").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAuthority(Constant.ROLE_ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority(Constant.ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority(Constant.ROLE_ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/cancel").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/admin/active-users").hasAuthority(Constant.ROLE_ADMIN)

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}