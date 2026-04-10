package com.ecommerce.api.service;

import com.ecommerce.api.dto.RegisterRequestDTO;
import com.ecommerce.api.dto.UpdateUserDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.entity.UserEntity;

import java.util.List;

/**
 * Service interface for managing user operations.
 * Provides methods for user registration, fetching,
 * updating, and deleting users.
 */
public interface UserService {


    /**
     * Registers a new user in the system.
     */
    UserEntity registerUser(RegisterRequestDTO dto);

    /**
     * Fetches users based on username, email, or phone.
     */
    List<UserResponseDTO> getUsers(String username, String email, String phoneNumber);
    /**
     * Fetches user by email.
     */
    UserResponseDTO getUserByEmail(String email);
    /**
     * Fetches user by username.
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * Fetches user by phone.
     */
    UserResponseDTO getUserByPhoneNumber(String phoneNumber);

    /**
     * Fetches allusers.
     */
    List<UserResponseDTO> getAllUsers();

    /**
     *
     * @return logged-in user's profile.
     */
    UserResponseDTO getMyProfile();

    /**
     * By Admin
     * @param username update user by username
     * @param dto updated user data
     * @return updated user
     */
    UserResponseDTO updateUserByUsername(String username, UpdateUserDTO dto);

    /**
     * Updates logged-in user's profile.
     */
    UserResponseDTO updateMyProfile(UpdateUserDTO dto);

    /**
     *
     * @param username delete user by username
     */
    void deleteUserByUsername(String username);


}