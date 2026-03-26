package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import Sb_new_project.demo.dto.LoginRequestDTO;
import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.security.JwtUtil;
import Sb_new_project.demo.service.ActiveUsersStore;
import Sb_new_project.demo.service.CustomUserDetailsService;
import Sb_new_project.demo.service.LoggedInUserService;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final ActiveUsersStore activeUsersStore;

    private final AuthenticationManager authenticationManager;
    private final LoggedInUserService loggedInUserService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register-user")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequestDTO dto)
    {
        log.info("User registration attempt: {}", dto.getUsername());

        User user = userService.registerUserAsUser(dto);

        log.info("User registered successfully: {}", user.getUsername());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody RegisterRequestDTO dto) {

        log.info("Admin registration attempt for: {}", dto.getUsername());

        boolean adminExists = userService.adminExists();

        if (!adminExists) {
            log.warn("No admin found. Creating first admin.");
            return ResponseEntity.ok(userService.registerUserAsAdmin(dto));
        }

        User admin = userService.registerUserAsAdmin(dto);

        log.info("Admin registered successfully: {}", admin.getUsername());

        return ResponseEntity.ok(admin);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {

        log.info("Login attempt for user: {}", dto.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(dto.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();

        LoggedInUserDTO loggedUser =
                new LoggedInUserDTO(userDetails.getUsername(), roles);

        activeUsersStore.addUser(token, loggedUser);

        log.info("Login successful for user: {}", dto.getUsername());

        return ResponseEntity.ok(token);
    }

    @GetMapping("/current-user")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(loggedInUserService.getCurrentUser());
    }
}