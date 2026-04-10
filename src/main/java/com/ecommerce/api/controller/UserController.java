package com.ecommerce.api.controller;

import com.ecommerce.api.dto.UpdateUserDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.service.UserService;
import com.ecommerce.api.util.AppConstants;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * API for get users ,get user by username, update user , delete users

 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get users based on filters
     */
    @GetMapping
    @RolesAllowed({AppConstants.ROLE_ADMIN})
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber) {

        log.info("Fetching users with filters username={}, email={}, phone={}",
              username, email, phoneNumber);

        return ResponseEntity.ok(userService.getUsers(username, email, phoneNumber));
    }
    /**
     * Get profile of currently logged-in user
     * @return current user profile details
     */
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN, AppConstants.ROLE_MANAGER})
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        log.info("Fetching current user profile");
        return ResponseEntity.ok(userService.getMyProfile());
    }

    /**
     * Update user details using username.
     *
     * @param dto contains updated user data
     * @return updated user response
     */
    @RolesAllowed({AppConstants.ROLE_ADMIN})
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@Valid  @RequestBody UpdateUserDTO dto) {
        UserResponseDTO updatedUser = userService.updateUserByUsername(dto.getUsername(), dto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Update current user profile.
     *
     * @param dto updated user details
     * @return updated user data
     */
    @PutMapping("/profile")
    @RolesAllowed({AppConstants.ROLE_USER, AppConstants.ROLE_ADMIN})
    public ResponseEntity<UserResponseDTO> updateMyProfile(@Valid @RequestBody UpdateUserDTO dto) {
        return ResponseEntity.ok(userService.updateMyProfile(dto));
    }

    /**
     * Delete user by username
     * @param username
     * @return
     */
    @RolesAllowed(AppConstants.ROLE_ADMIN)
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        log.info("deleting user by admin:{}",username);
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok(String.format(AppConstants.USER_DELETED_SUCCESS, username));
    }

}