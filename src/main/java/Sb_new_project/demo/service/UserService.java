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

    User getUserByUsername(String username);

    User getMyProfile(Authentication authentication);

    User updateUserByUsername(String username, UpdateUserDTO dto);

    User updateMyProfile(Authentication authentication, UpdateUserDTO dto);

    void deleteUserByUsername(String username);
}