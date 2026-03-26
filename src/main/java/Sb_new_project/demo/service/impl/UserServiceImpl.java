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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Override
    @Transactional
    public User registerUserAsUser(RegisterRequestDTO dto) {
        log.info("Registering USER: {}", dto.getUsername());

        return createUser(dto, Constant.ROLE_USER);
    }

    @Override
    @Transactional
    public User registerUserAsAdmin(RegisterRequestDTO dto) {
        log.info("Registering ADMIN: {}", dto.getUsername());
        return createUser(dto, Constant.ROLE_ADMIN);
    }

    @Override
    public boolean adminExists() {
        return userRepository.existsByRole_RoleName(Constant.ROLE_ADMIN);
    }



    private User createUser(RegisterRequestDTO dto, String roleName) {

        validateUser(dto);

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


        log.info("User created successfully: {}", user.getUsername());


        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + id));
    }

    @Override
    public User getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));
    }

    @Override
    @Transactional
    public User updateUser(Long id, RegisterRequestDTO dto) {

        log.info("Updating user with ID: {}", id);

        User user = getUserById(id);

        if (!StringUtils.isEmpty(dto.getUsername())) {
            if (!user.getUsername().equals(dto.getUsername())
                    && userRepository.existsByUsername(dto.getUsername())) {
                throw new BadRequestException(Constant.USERNAME_EXISTS);
            }
            user.setUsername(dto.getUsername().trim());
        }

        if (!StringUtils.isEmpty(dto.getEmail())) {
            if (!user.getEmail().equals(dto.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                throw new BadRequestException(Constant.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(dto.getEmail().trim());
        }

        if (!StringUtils.isEmpty(dto.getPassword())) {
            if (dto.getPassword().length() < 6) {
                throw new BadRequestException(Constant.PASSWORD_INVALID);
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setUpdatedBy(loggedInUserServiceImpl.getUsername());
        user.setUpdatedDate(java.time.LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserByUsername(String username, RegisterRequestDTO dto) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.USER_NOT_FOUND + username));

        if (!StringUtils.isEmpty(dto.getUsername())) {
            if (!user.getUsername().equals(dto.getUsername())
                    && userRepository.existsByUsername(dto.getUsername())) {
                throw new BadRequestException(Constant.USERNAME_EXISTS);
            }
            user.setUsername(dto.getUsername().trim());
        }

        if (!StringUtils.isEmpty(dto.getEmail())) {
            if (!user.getEmail().equals(dto.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                throw new BadRequestException(Constant.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(dto.getEmail().trim());
        }

        if (!StringUtils.isEmpty(dto.getPassword())) {
            if (dto.getPassword().length() < 6) {
                throw new BadRequestException(Constant.PASSWORD_INVALID);
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setUpdatedBy(loggedInUserServiceImpl.getUsername());
        user.setUpdatedDate(java.time.LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = getUserById(id);
        userRepository.delete(user);
    }

    private void validateUser(RegisterRequestDTO dto) {

        if (dto == null) {
            throw new BadRequestException(Constant.BAD_REQUEST);
        }

        if (StringUtils.isEmpty(dto.getUsername())) {
            throw new BadRequestException(Constant.USERNAME_REQUIRED);
        }

        if (StringUtils.isEmpty(dto.getEmail())) {
            throw new BadRequestException(Constant.EMAIL_REQUIRED);
        }

        if (StringUtils.isEmpty(dto.getPassword()) || dto.getPassword().length() < 6) {
            throw new BadRequestException(Constant.PASSWORD_INVALID);
        }
    }
}