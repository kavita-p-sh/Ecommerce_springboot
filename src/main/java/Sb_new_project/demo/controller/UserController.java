package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<User> getMyProfile(Authentication authentication) {

        String username = authentication.getName();
        log.info("Fetching profile for user [{}]", username);

        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/profile")
    @RolesAllowed({Constant.ROLE_USER, Constant.ROLE_ADMIN})
    public ResponseEntity<User> updateMyProfile(Authentication authentication,
                                                @RequestBody RegisterRequestDTO dto) {

        String username = authentication.getName();
        log.info("Updating profile for user [{}]", username);

        User updatedUser = userService.updateUserByUsername(username, dto); // ✅ updated
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<List<User>> getUsers() {

        log.info("Admin fetching all users");

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        log.info("Admin fetching user with id [{}]", id);

        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{id}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @Valid
                                           @RequestBody RegisterRequestDTO dto) {

        log.info("Admin updating user with id [{}]", id);

        User updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        log.warn("Admin deleting user with id [{}]", id);

        userService.deleteUser(id);
        return ResponseEntity.ok(Constant.USER_DELETED_SUCCESS + id);
    }
}