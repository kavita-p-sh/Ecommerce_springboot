package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UpdateUserDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.entity.RoleEntity;
import Sb_new_project.demo.entity.UserEntity;
import Sb_new_project.demo.enums.RoleName;
import Sb_new_project.demo.exception.BadRequestException;
import Sb_new_project.demo.exception.UserNotFoundException;
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
import org.springframework.http.ResponseEntity;
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


    private UserEntity createUser(RegisterRequestDTO dto, RoleName roleName) {
        log.info("Register request for: {}", dto.getUsername());

        validateUserNotExists(dto);

        RoleEntity role = getValidRole(roleName);

        UserEntity user = buildUser(dto, role);

        log.info("registered successfully");
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

    private RoleEntity getValidRole(RoleName roleName) {
        log.debug("Fetching role: {}", roleName);
        RoleEntity role = roleRepository.findByRoleName(roleName);

        if (role == null) {
            throw new UserNotFoundException(Constant.ROLE_NOT_FOUND + roleName);
        }
        return role;
    }

    private UserEntity buildUser(RegisterRequestDTO dto, RoleEntity role) {

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail().trim());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        log.debug("User object created successfully for username: {}", dto.getUsername());

        return user;
    }

    @Override
    @Transactional
    public UserEntity registerUser(RegisterRequestDTO dto) {

        log.info("Register request received for username: {}", dto.getUsername());

        RoleName roleName = resolveAndValidateRole(dto.getRole());

        return createUser(dto, roleName);
    }

    /**
     * Validate role.
     */
    private RoleName resolveAndValidateRole(RoleName requestedRole) {

        if (requestedRole == null) {
            return RoleName.ROLE_USER;
        }

        List<RoleName> allowedRoles = List.of(
                RoleName.ROLE_USER,
                RoleName.ROLE_ADMIN,
                RoleName.ROLE_MANAGER
        );

        if (!allowedRoles.contains(requestedRole)) {
            throw new RuntimeException(Constant.ROLE_NOT_FOUND);
        }

        return requestedRole;
    }
    @Override
    public List<UserResponseDTO> getUsers(Long userid, String username, String email) {

        if (userid != null) {
            return List.of(getUserById(userid));
        }

        if (StringUtils.hasText(username)) {
            return List.of(getUserByUsername(username));
        }

        if (StringUtils.hasText(email)) {
            return List.of(getUserByEmail(email));
        }

        return getAllUsers();
    }

    @Override
    public UserResponseDTO getUserById(Long userid) {
        UserEntity user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException(Constant.USER_NOT_FOUND + userid));

        return mapToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(Constant.USER_NOT_FOUND + username));

        return mapToUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(Constant.USER_NOT_FOUND + email));

        return mapToUserResponseDTO(user);
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = "user", key = "'allUsers'")
    public List<UserResponseDTO> getAllUsers() {

        log.info("Fetching all users from database");

        List<UserEntity> users = userRepository.findAll();

        return users.stream()
                .map(user -> mapToUserResponseDTO(user))
                .toList();
    }

    @Override
    public UserResponseDTO getMyProfile(Authentication authentication) {
        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND + username));

        return mapToUserResponseDTO(user);

    }


    @Override
    @Transactional
    @CachePut(cacheNames = "user", key = "#dto.username")
    public UserResponseDTO updateUserByUsername(String username, UpdateUserDTO dto) {
        log.info("Updating user with username: {}", username);

        UserEntity existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND + username));

        if (!StringUtils.isEmpty(dto.getEmail())) {
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            existingUser.setPhoneNumber(dto.getPhoneNumber());
        }

        UserEntity updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully: {}", username);
        return mapToUserResponseDTO(updatedUser);

    }

    @Override
    @Transactional
    @CachePut(cacheNames = "user", key = "#result.username")
    public UserResponseDTO updateMyProfile(Authentication authentication, UpdateUserDTO dto) {
        String username = authentication.getName();

        log.info("Updating profile for user: {}", username);

        UserEntity existingUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(Constant.USER_NOT_FOUND + username));

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            existingUser.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            existingUser.setPhoneNumber(dto.getPhoneNumber());
        }

        UserEntity updatedUser = userRepository.save(existingUser);

        log.info("Profile updated successfully: {}", username);

        return mapToUserResponseDTO(updatedUser);
    }

    @Override
    public void deleteUserByUsername(String username) {
        log.info("Deleting user with username: {}", username);

        if (!loggedInUserServiceImpl.isAdmin()) {
            throw new BadRequestException(Constant.ONLY_ADMIN_ALLOWED);
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(Constant.USER_NOT_FOUND + username));

        userRepository.delete(user);

        log.info(Constant.USER_DELETED_SUCCESS, username);
    }

    private UserResponseDTO mapToUserResponseDTO(UserEntity user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole().getRoleName());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setUpdatedBy(user.getUpdatedBy());
        return dto;
    }


}