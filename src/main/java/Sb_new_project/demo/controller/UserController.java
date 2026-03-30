package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 * <p>
 * Provides APIs for:
 * - User profile management
 * - Admin operations (CRUD)
 * - Role-based access control
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Fetch the profile of the currently authenticated user
     */
    @GetMapping("/profile")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyProfile(authentication));
    }

    @GetMapping
    @RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/username/{username}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/username/{username}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<User> updateUser(@PathVariable String username,
                                           @Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUserByUsername(username, dto));
    }

    @PutMapping("/profile")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<User> updateMyProfile(Authentication authentication,
                                                @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(userService.updateMyProfile(authentication, dto));
    }

    @DeleteMapping("/username/{username}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok(Constant.USER_DELETED_SUCCESS + username);
    }
}