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

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        log.info("Login request for user: " + dto.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(dto.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        return new LoginResponseDTO(token);
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO dto) {

        log.info("Register user: " + dto.getUsername());
        User user = userService.registerUser(dto);

        UserResponseDTO response = new UserResponseDTO();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole().getRoleName());

        return response;
    }
}