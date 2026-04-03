package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UpdateUserDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @RolesAllowed(Constant.ROLE_ADMIN)
    @GetMapping("/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        log.info("fetching user by username: {}", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getMyProfile(Authentication authentication) {
        log.info("fetching user profile");
        return ResponseEntity.ok(userService.getMyProfile(authentication));
    }

    @RolesAllowed(Constant.ROLE_ADMIN)
    @PutMapping
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UpdateUserDTO dto) {
        UserResponseDTO updatedUser = userService.updateUserByUsername(dto.getUsername(), dto);
        return ResponseEntity.ok(updatedUser);
    }

    @RolesAllowed(Constant.ROLE_ADMIN)
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        log.info("deleting user by admin:{}",username);
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok("User" + username + Constant.USER_DELETED_SUCCESS);
    }

}