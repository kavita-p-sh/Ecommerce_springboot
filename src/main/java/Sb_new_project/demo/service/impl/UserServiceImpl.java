package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.entity.Role;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.exception.BadRequestException;
import Sb_new_project.demo.exception.ResourceNotFoundException;
import Sb_new_project.demo.repository.RoleRepository;
import Sb_new_project.demo.repository.UserRepository;
import Sb_new_project.demo.service.UserService;
import Sb_new_project.demo.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Implementation of UserService.
 * Handles business logic related to user operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoggedInUserServiceImpl loggedInUserServiceImpl;

    /**
     * Register a new user.
     */
    @Override
    @Transactional
    public User registerUser(RegisterRequestDTO dto) {

        log.info("Register request for: {}", dto.getUsername());

        String roleName = resolveAndValidateRole(dto.getRole());

        return createUser(dto, roleName);
    }

    /**
     * Create and save user.
     */
    private User createUser(RegisterRequestDTO dto, String roleName) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException(Constant.USERNAME_EXISTS);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException(Constant.EMAIL_ALREADY_EXISTS);
        }

        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new ResourceNotFoundException(Constant.ROLE_NOT_FOUND + roleName);
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * Validate role.
     */
    private String resolveAndValidateRole(String requestedRole) {

        if (!StringUtils.hasText(requestedRole)) {
            return Constant.ROLE_USER;
        }

        String roleName = requestedRole.trim().toUpperCase();

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

    /**
     * Get all users.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by username.
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));
    }

    /**
     * Get logged-in user profile.
     */
    @Override
    public User getMyProfile(Authentication authentication) {
        return getUserByUsername(authentication.getName());
    }

    /**
     * Update logged-in user profile.
     */
    @Override
    @Transactional
    public User updateMyProfile(Authentication authentication, RegisterRequestDTO dto) {
        return updateUserByUsername(authentication.getName(), dto);
    }

    /**
     * Update user by username.
     */
    @Override
    @Transactional
    public User updateUserByUsername(String username, RegisterRequestDTO dto) {

        User user = getUserByUsername(username);

        updateFields(user, dto);

        user.setUpdatedBy(loggedInUserServiceImpl.getUsername());
        user.setUpdatedDate(java.time.LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Update user fields.
     */
    private void updateFields(User user, RegisterRequestDTO dto) {

        if (StringUtils.isEmpty(dto.getUsername()) &&
                !user.getUsername().equals(dto.getUsername())) {

            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new BadRequestException(Constant.USERNAME_EXISTS);
            }
            user.setUsername(dto.getUsername().trim());
        }

        if (StringUtils.isEmpty(dto.getEmail()) &&
                !user.getEmail().equals(dto.getEmail())) {

            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new BadRequestException(Constant.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(dto.getEmail().trim());
        }

        if (StringUtils.isEmpty(dto.getPassword())) {

            if (dto.getPassword().length() < 6) {
                throw new BadRequestException(Constant.PASSWORD_INVALID);
            }

            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
    }

    /**
     * Delete user by username.
     */
    @Override
    @Transactional
    public void deleteUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));

        userRepository.delete(user);
    }
}