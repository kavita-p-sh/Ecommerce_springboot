package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UpdateUserDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.entity.RoleEntity;
import com.ecommerce.api.entity.UserEntity;
import com.ecommerce.api.enums.RoleName;
import com.ecommerce.api.exception.BadRequestException;
import com.ecommerce.api.mapper.UserMapper;
import com.ecommerce.api.repository.RoleRepository;
import com.ecommerce.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test class for userServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    /**
     * Mocked UserRepository.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked RoleRepository
     */
    @Mock
    private RoleRepository roleRepository;

    /**
     * mocked Password
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Mocked Logged in user
     */
    @Mock
    private LoggedInUserServiceImpl loggedInUserServiceImpl;

    /**
     * Mocked UserMapper to convert entity to DTO.
     */
    @Mock
    private UserMapper userMapper;

    /**
     * Injects mocks into UserServiceImpl
     */
    @InjectMocks
    private UserServiceImpl userService;


    /**
     * Tests Register user Successfully
     */
    @Test
    void registerUser_success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("ShriYadav");
        dto.setEmail("shriyd@gmail.com");
        dto.setPhoneNumber("9876543210");
        dto.setPassword("pass");
        dto.setRole(RoleName.USER);

        RoleEntity role = new RoleEntity();
        role.setRoleName(RoleName.USER);

        UserEntity saved = new UserEntity();
        saved.setUsername("ShriYadav");

        UserResponseDTO response = new UserResponseDTO();
        response.setUsername("ShriYadav");

        when(userRepository.existsByUsername("ShriYadav")).thenReturn(false);
        when(userRepository.existsByEmail("shriyd@gmail.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("9876543210")).thenReturn(false);

        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(saved);
        when(userMapper.toDTO(saved)).thenReturn(response);

        UserResponseDTO result = userService.registerUser(dto);

        assertEquals("ShriYadav", result.getUsername());
    }

    /**
     * Tests while register user is already exists
     */
    @Test
    void registerUser_usernameExists() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("ShriYadav");
        dto.setRole(RoleName.USER);

        when(userRepository.existsByUsername("ShriYadav")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(dto));
    }

    /**
     * Tests fetching user by username successfully
     */

    @Test
    void getUsers_byUsername() {
        UserEntity user = new UserEntity();
        UserResponseDTO dto = new UserResponseDTO();

        when(userRepository.findByUsername("ShriYadav"))
                .thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        List<UserResponseDTO> result =
                userService.getUsers("ShriYadav", null, null);

        assertEquals(1, result.size());
    }


    /**
     * Tests Get all users
     */
    @Test
    void getUsers_all() {
        UserEntity user = new UserEntity();
        UserResponseDTO dto = new UserResponseDTO();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        List<UserResponseDTO> result = userService.getUsers(null, null, null);

        assertEquals(1, result.size());
    }

    /**
     * Tests get logged in user profile
     */
    @Test
    void getMyProfile_success() {
        when(loggedInUserServiceImpl.getUsername()).thenReturn("ShriYadav");

        UserEntity user = new UserEntity();
        UserResponseDTO dto = new UserResponseDTO();

        when(userRepository.findByUsername("ShriYadav"))
                .thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        UserResponseDTO result = userService.getMyProfile();

        assertNotNull(result);
    }

    /**
     * Tests Update user by username
     */

    @Test
    void updateUserByUsername_success() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail("shriyadav12367@gmail.com");

        UserEntity user = new UserEntity();
        user.setEmail("old@gmail.com");

        UserResponseDTO response = new UserResponseDTO();

        when(userRepository.findByUsername("ShriYadav"))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("shriyadav12367@gmail.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(response);

        UserResponseDTO result =
                userService.updateUserByUsername("ShriYadav", dto);

        assertNotNull(result);
    }

    /**
     * Tests update profile
     */
    @Test
    void updateMyProfile_success() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setPhoneNumber("9023782363");

        when(loggedInUserServiceImpl.getUsername()).thenReturn("ShriYadav");

        UserEntity user = new UserEntity();
        user.setPhoneNumber("8828771209");

        UserResponseDTO response = new UserResponseDTO();

        when(userRepository.findByUsername("ShriYadav"))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByPhoneNumber("9023782363")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(response);

        UserResponseDTO result = userService.updateMyProfile(dto);

        assertNotNull(result);
    }

    /**
     * Tests Delete user by username
     */
    @Test
    void deleteUserByUsername_success() {
        UserEntity user = new UserEntity();

        when(userRepository.findByUsername("ShriYadav"))
                .thenReturn(Optional.of(user));

        userService.deleteUserByUsername("ShriYadav");

        verify(userRepository).delete(user);
    }

}