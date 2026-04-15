package com.ecommerce.api.controller;

import com.ecommerce.api.dto.UpdateUserDTO;
import com.ecommerce.api.dto.UserResponseDTO;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.service.UserService;
import com.ecommerce.api.util.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * unit test class for User Controller* It verifies:
 * - HTTP status codes returned by APIs
 * - Response body content
 *
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest{

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    /**
     * Tests fetching users with email filter.
     */
    @Test
    void getUser_WithEmail_success(){
        UserResponseDTO response= new UserResponseDTO();
        response.setUsername("RamPatel");
        response.setEmail("ram3413@gmail.com");
        response.setPhoneNumber("9378478949");
        when(userService.getUsers(null, "ram3413@gmail.com", null)).thenReturn(List.of(response));

        ResponseEntity<List<UserResponseDTO>> result = userController.getUsers(null, "ram3413@gmail.com", null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("RamPatel", result.getBody().get(0).getUsername());
        assertEquals("ram3413@gmail.com", result.getBody().get(0).getEmail());
        assertEquals("9378478949", result.getBody().get(0).getPhoneNumber());
    }


    /**
     * Tests fetching all users without any filters.
     */
    @Test
    void getAllUsers_success() {

        UserResponseDTO user1 = new UserResponseDTO();
        user1.setUsername("RamPatel");
        user1.setEmail("ram3413@gmail.com");

        UserResponseDTO user2 = new UserResponseDTO();
        user2.setUsername("ShyamVerma");
        user2.setEmail("shyam@gmail.com");

        when(userService.getUsers(null, null, null))
                .thenReturn(List.of(user1, user2));

        ResponseEntity<List<UserResponseDTO>> result =
                userController.getUsers(null, null, null);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());
        assertEquals("RamPatel", result.getBody().get(0).getUsername());
    }

    /**
     * Tests service exception while fetching users.
     */
    @Test
    void getUsers_notFound_throwsResourceNotFoundException() {
        when(userService.getUsers(null, "email1234@gmail.com", null))
                .thenThrow(new ResourceNotFoundException("User not found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userController.getUsers(null, "email124@gmail.com", null)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userService).getUsers(null, "email124@gmail.com", null);
    }


    /**
     * Tests Get my profile of the current user.
     */
    @Test
    void getMyProfile_success() {

        UserResponseDTO response = new UserResponseDTO();
        response.setUsername("RamPatel");
        response.setEmail("ram3413@gmail.com");
        response.setPhoneNumber("9378478949");

        when(userService.getMyProfile()).thenReturn(response);

        ResponseEntity<UserResponseDTO> result = userController.getMyProfile();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("RamPatel", result.getBody().getUsername());
        assertEquals("ram3413@gmail.com", result.getBody().getEmail());
        assertEquals("9378478949", result.getBody().getPhoneNumber());
    }

    /**
     * Tests updating profile of the current user.
     */
    @Test
    void updateUser_success() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setUsername("RamPatel");
        dto.setEmail("newram@gmail.com");
        dto.setPhoneNumber("9876543210");

        UserResponseDTO updatedResponse = new UserResponseDTO();
        updatedResponse.setUsername("RamPatel");
        updatedResponse.setEmail("newram@gmail.com");
        updatedResponse.setPhoneNumber("9876543210");

        when(userService.updateUserByUsername("RamPatel", dto))
                .thenReturn(updatedResponse);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("RamPatel", response.getBody().getUsername());
        assertEquals("newram@gmail.com", response.getBody().getEmail());

    }

    /**
     *  Tests deleting a user by username.
     *
     */
    @Test
    void deleteUser_success() {
        String username = "RamPatel";

        doNothing().when(userService).deleteUserByUsername(username);

        ResponseEntity<String> response = userController.deleteUser(username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(String.format(AppConstants.USER_DELETED_SUCCESS, username), response.getBody());

        verify(userService, times(1)).deleteUserByUsername(username);
    }

    /**
     * Tests delete failure when user does not exist.
     */
    @Test
    void deleteUser_notFound() {
        String username = "UnknownUser";

        doThrow(new ResourceNotFoundException("User not found with username: " + username))
                .when(userService).deleteUserByUsername(username);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userController.deleteUser(username)
        );

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userService).deleteUserByUsername(username);
    }


}