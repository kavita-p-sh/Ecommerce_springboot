package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UpdateUserDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.entity.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing user operations.
 * Provides methods for user registration, fetching,
 * updating, and deleting users.
 */
public interface UserService {

    UserEntity registerUser(RegisterRequestDTO dto);

    List<UserResponseDTO> getUsers(UserResponseDTO filter);

    UserResponseDTO getUserByEmail(String email);

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO getUserByPhoneNumber(String phoneNumber);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getMyProfile(Authentication authentication);

    UserResponseDTO updateUserByUsername(String username, UpdateUserDTO dto);

    UserResponseDTO updateMyProfile(Authentication authentication, UpdateUserDTO dto);

    void deleteUserByUsername(String username);
}