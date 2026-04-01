package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UpdateUserDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.entity.Role;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.exception.BadRequestException;
import Sb_new_project.demo.exception.ResourceNotFoundException;
import Sb_new_project.demo.repository.RoleRepository;
import Sb_new_project.demo.repository.UserRepository;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;

    private User createUser(RegisterRequestDTO dto, String roleName) {
        log.info("Register request for: {}", dto.getUsername());
        validateUserNotExists(dto);
        Role role = getValidRole(roleName);
        User user = buildUser(dto, role);

        return userRepository.save(user);
    }

    private void validateUserNotExists(RegisterRequestDTO dto) {

        log.debug("Checking if user already exists: {}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            log.error("Username already exists: {}", dto.getUsername());
            throw new BadRequestException(Constant.USERNAME_EXISTS);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.error("Email already exists: {}", dto.getEmail());
            throw new BadRequestException(Constant.EMAIL_ALREADY_EXISTS);
        }
    }

    private Role getValidRole(String roleName) {
        log.debug("Fetching role: {}", roleName);
        Role role = roleRepository.findByRoleName(roleName);

        if (role == null) {
            throw new ResourceNotFoundException(Constant.ROLE_NOT_FOUND + roleName);
        }
        return role;
    }

    private User buildUser(RegisterRequestDTO dto, Role role) {

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        log.debug("User object created successfully for username: {}", dto.getUsername());

        return user;
    }

    @Override
    @CachePut(cacheNames = "user", key = "username")
    public User registerUser(RegisterRequestDTO dto) {

        log.info("Register request received for username: {}", dto.getUsername());

        String roleName = resolveAndValidateRole(dto.getRole());

        return createUser(dto, roleName);
    }

    /**
     * Validate role.
     */
    private String resolveAndValidateRole(String requestedRole) {

        if (StringUtils.isEmpty(requestedRole)) {
            return Constant.ROLE_USER;
        }
        String roleName = requestedRole;

        List<String> allowedRoles = List.of(
                Constant.ROLE_USER,
                Constant.ROLE_ADMIN,
                Constant.ROLE_MANAGER
        );

        if (!allowedRoles.contains(roleName)) {
            throw new BadRequestException(Constant.INVALID_ROLE);
        }

        return roleName;
    }
    @Override
    @Cacheable(cacheNames = "user", key = "'allUsers'")
    public List<UserResponseDTO> getAllUsers() {

        log.info("Fetching all users from database");

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserResponseDTO(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().getRoleName()

                ))
                .toList();
    }

    @Override
    @Cacheable(cacheNames = "user", key = "'allUsers'")
    public User getUserByUsername(String username)  {

        log.info("Fetching user by username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(Constant.USER_NOT_FOUND_WITH_USERNAME + username)
                );
    }

    @Override
    public User getMyProfile(Authentication authentication) {
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));
    }

    @Override
    @CachePut(cacheNames = "user", key = "#username")
    public User updateUserByUsername(String username, UpdateUserDTO dto) {
        log.info("Updating user with username: {}", username);

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));

        if (StringUtils.isEmpty(dto.getUsername())) {
            existingUser.setUsername(dto.getUsername());
        }

        if (StringUtils.isEmpty(dto.getEmail())) {
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully: {}", username);
        return updatedUser;
    }

    @Override
    @CachePut(cacheNames = "user", key = "#result.username")
    public User updateMyProfile(Authentication authentication, UpdateUserDTO dto) {
        String username = authentication.getName();

        log.info("Updating profile for user: {}", username);

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            existingUser.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);

        log.info("Profile updated successfully: {}", username);

        return updatedUser;
    }

    @Override
    public void deleteUserByUsername(String username) {
        log.info("Deleting user with username: {}", username);

        if (!loggedInUserServiceImpl.isAdmin()) {
            throw new BadRequestException(Constant.ONLY_ADMIN_ALLOWED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));

        userRepository.delete(user);

        log.info(Constant.USER_DELETED_SUCCESS, username);
    }


}