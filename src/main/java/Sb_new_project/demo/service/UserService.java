package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UpdateUserDTO;
import Sb_new_project.demo.dto.UserResponseDTO;
import Sb_new_project.demo.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing user operations.
 * <p>
 * Provides methods for:
 * - User registration
 * - Fetching user details
 * - Updating user information
 * - Deleting users
 * </p>
 */
public interface UserService {


    User registerUser(RegisterRequestDTO dto);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserByUsername(String username);

    UserResponseDTO getMyProfile(Authentication authentication);

    UserResponseDTO updateUserByUsername(String username, UpdateUserDTO dto);

    UserResponseDTO updateMyProfile(Authentication authentication, UpdateUserDTO dto);

    void deleteUserByUsername(String username);
}