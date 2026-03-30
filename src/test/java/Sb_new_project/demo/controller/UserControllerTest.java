//package Sb_new_project.demo.controller;
//
//import Sb_new_project.demo.dto.RegisterRequestDTO;
//import Sb_new_project.demo.entity.User;
//import Sb_new_project.demo.service.impl.UserServiceImpl;
//import Sb_new_project.demo.util.Constant;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//
//
//    @Mock
//    private UserServiceImpl userServiceImpl;
//
//    @InjectMocks
//    private UserController userController;
//
//    private static final String USERNAME_TEST = "kavita";
//    private static final String ADMIN_TEST = "admin";
//    private static final String UPDATED_TEST = "updated";
//
//    @Test
//    void testGetMyProfile() {
//
//        User user = new User();
//        user.setUsername(USERNAME_TEST);
//
//        when(userServiceImpl.getUserByUsername(USERNAME_TEST)).thenReturn(user);
//
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(USERNAME_TEST);
//
//        ResponseEntity<User> response = userController.getMyProfile(auth);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(USERNAME_TEST, response.getBody().getUsername());
//
//        verify(userServiceImpl).getUserByUsername(USERNAME_TEST);
//    }
//
//    @Test
//    void testUpdateMyProfile() {
//
//        RegisterRequestDTO dto = new RegisterRequestDTO();
//        dto.setUsername(USERNAME_TEST);
//
//        User user = new User();
//        user.setUsername(USERNAME_TEST);
//
//        when(userServiceImpl.updateUserByUsername(eq(USERNAME_TEST), any()))
//                .thenReturn(user);
//
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn(USERNAME_TEST);
//
//        ResponseEntity<User> response =
//                userController.updateMyProfile(auth, dto);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(USERNAME_TEST, response.getBody().getUsername());
//
//        verify(userServiceImpl).updateUserByUsername(eq(USERNAME_TEST), any());
//    }
//
//    @Test
//    void testGetAllUsers() {
//
//        User user = new User();
//        user.setUsername(ADMIN_TEST);
//
//        when(userServiceImpl.getAllUsers()).thenReturn(List.of(user));
//
//        ResponseEntity<List<User>> response = userController.getUsers();
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertFalse(response.getBody().isEmpty());
//        assertEquals(ADMIN_TEST, response.getBody().get(0).getUsername());
//
//        verify(userServiceImpl).getAllUsers();
//    }
//
//    @Test
//    void testGetUserById() {
//
//        User user = new User();
//        user.setUserId(1L);
//        user.setUsername(USERNAME_TEST);
//
//        when(userServiceImpl.getUserById(1L)).thenReturn(user);
//
//        ResponseEntity<User> response = userController.getUserById(1L);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1L, response.getBody().getUserId());
//
//        verify(userServiceImpl).getUserById(1L);
//    }
//
//    @Test
//    void testGetUserById_Exception() {
//
//        when(userServiceImpl.getUserById(1L))
//                .thenThrow(new RuntimeException(Constant.USER_NOT_FOUND));
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
//            userController.getUserById(1L);
//        });
//
//        assertEquals(Constant.USER_NOT_FOUND, ex.getMessage());
//
//        verify(userServiceImpl).getUserById(1L);
//    }
//
//    @Test
//    void testUpdateUser() {
//
//        RegisterRequestDTO dto = new RegisterRequestDTO();
//        dto.setUsername(UPDATED_TEST);
//
//        User user = new User();
//        user.setUsername(UPDATED_TEST);
//
//        when(userServiceImpl.updateUser(eq(1L), any())).thenReturn(user);
//
//        ResponseEntity<User> response =
//                userController.updateUser(1L, dto);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(UPDATED_TEST, response.getBody().getUsername());
//
//        verify(userServiceImpl).updateUser(eq(1L), any());
//    }
//
//    @Test
//    void testDeleteUser() {
//
//        doNothing().when(userServiceImpl).deleteUser(1L);
//
//        ResponseEntity<String> response =
//                userController.deleteUser(1L);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(Constant.USER_DELETED_SUCCESS + 1L, response.getBody());
//
//        verify(userServiceImpl).deleteUser(1L);
//    }
//
//
//}
