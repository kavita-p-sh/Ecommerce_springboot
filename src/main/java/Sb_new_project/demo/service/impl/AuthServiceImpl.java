package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.LoginRequestDTO;
import Sb_new_project.demo.dto.LoginResponseDTO;
import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.security.JwtUtil;
import Sb_new_project.demo.service.AuthService;
import Sb_new_project.demo.service.CustomUserDetailsService;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Handles login and registration functionality.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Authenticates the user and generates JWT token.
     *
     * @param dto login request containing username and password
     * @return LoginResponseDTO containing JWT token, username, and roles
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        log.info("Login attempt for user: {}", dto.getUsername());

        authenticate(dto);

        UserDetails userDetails = loadUser(dto.getUsername());

        String token = generateToken(userDetails);

        List<String> roles = extractRoles(userDetails);

        log.info("Login successful for user: {}", dto.getUsername());

        return new LoginResponseDTO(
                token,
                userDetails.getUsername(),
                roles
        );
    }


    @Override
    public UserResponseDTO register(RegisterRequestDTO dto) {

        log.info("Registering new user: {}", dto.getUsername());

        User user = userService.registerUser(dto);

        log.info("User registered successfully: {}", dto.getUsername());

        UserResponseDTO res = new UserResponseDTO();
        res.setUserId(user.getUserId());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole().getRoleName());

        return res;
    }

    private void authenticate(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );
    }

    private UserDetails loadUser(String username) {
        return customUserDetailsService.loadUserByUsername(username);
    }


    private String generateToken(UserDetails userDetails) {
        return jwtUtil.generateToken(userDetails);
    }

    private List<String> extractRoles(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();
    }
}