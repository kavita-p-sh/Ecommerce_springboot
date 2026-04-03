package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.LoginRequestDTO;
import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.service.AuthService;
import Sb_new_project.demo.service.LoggedInUserService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Handles authentication APIs like login, register, profile and logout.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoggedInUserService loggedInUserService;


    /**
     * Register new user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    /**
     * Login user and return token.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Logout user.
     */
    @PostMapping("/logout")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(Constant.LOGOUT_SUCCESS);
    }
}