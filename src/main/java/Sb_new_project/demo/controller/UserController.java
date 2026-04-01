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
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("fetching user by username");
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    @GetMapping("/profile")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        log.info("fetching user profile");
        return ResponseEntity.ok(userService.getMyProfile(authentication));
    }

    @RolesAllowed(Constant.ROLE_ADMIN)
    @PutMapping("/{username}")
    public ResponseEntity<User> updateUserByUsername(@PathVariable String username, @RequestBody UpdateUserDTO dto) {
        log.info("update user by admin");
        return ResponseEntity.ok(userService.updateUserByUsername(username, dto));
    }

    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN, Constant.ROLE_MANAGER})
    @PutMapping("/update/profile")
    public ResponseEntity<User> updateMyProfile(
            Authentication authentication,
            @RequestBody UpdateUserDTO dto) {
        return ResponseEntity.ok(userService.updateMyProfile(authentication, dto));
    }

    @RolesAllowed(Constant.ROLE_ADMIN)
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        log.info("deleting user by admin");
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok(Constant.USER_DELETED_SUCCESS);
    }
}