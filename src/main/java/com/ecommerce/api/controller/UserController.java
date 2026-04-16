package com.ecommerce.api.controller;

import com.ecommerce.api.dto.UpdateUserDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.service.UserService;
import com.ecommerce.api.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Controller", description = "APIs for managing users and profiles")
public class UserController {

    private final UserService userService;

    /**
     * Get users based on filters
     */
    @GetMapping
    @RolesAllowed({AppConstants.ROLE_ADMIN})
    @Operation(summary = "Get users with filters",
            description = "Fetches users based on optional filters like username, email, and phone number")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "user fetched successfully",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403",description = "permission denied")
    })
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
    @Operation(
            summary = "Get current user profile",
            description = "Fetches the profile details of the currently logged-in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Permission denied")
    })
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
    @Operation(summary = "Update user by username",
            description = "Updates user details using username, Only Admin can update user")
    @ApiResponses({@ApiResponse(responseCode = "200",description = "User update successfully",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
    ),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Permission denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Update current user profile",
            description = "Updates the profile details of the currently logged-in user.")
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
    @Operation(summary = "Delete user by username",
            description = "Deletes a user using username. Only admin can delete users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Permission denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        log.info("deleting user by admin:{}",username);
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok(String.format(AppConstants.USER_DELETED_SUCCESS, username));
    }

}