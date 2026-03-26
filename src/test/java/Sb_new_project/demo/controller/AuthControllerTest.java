package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.LoginRequestDTO;
import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.security.JwtUtil;
import Sb_new_project.demo.service.impl.CustomUserDetailsServiceImple;
import Sb_new_project.demo.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsServiceImple customUserDetailsServiceImple;

    @InjectMocks
    private AuthController authController;



    @Test
    void testRegisterUser() {

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername("kavita123");
        dto.setPassword("password123");
        dto.setEmail("kavita123@gmail.com");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("kavita123");
        user.setEmail("kavita123@gmail.com");

        Mockito.when(userServiceImpl.registerUserAsUser(Mockito.any()))
                .thenReturn(user);

        ResponseEntity<User> response = authController.registerUser(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("kavita123", response.getBody().getUsername());
    }

    @Test
    void testLogin_Success() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("kavita");
        dto.setPassword("123");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(null);

        Mockito.when(customUserDetailsServiceImple.loadUserByUsername("kavita"))
                .thenReturn(userDetails);

        Mockito.when(jwtUtil.generateToken(userDetails))
                .thenReturn("test-token");

        ResponseEntity<?> response = authController.login(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test-token", response.getBody());

        Mockito.verify(authenticationManager).authenticate(Mockito.any());
        Mockito.verify(customUserDetailsServiceImple).loadUserByUsername("kavita");
    }

    @Test
    void testLogin_InvalidCredentials() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("kavita");
        dto.setPassword("wrong");

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        Exception ex = assertThrows(BadCredentialsException.class, () ->
                authController.login(dto)
        );

        assertEquals("Bad credentials", ex.getMessage());

        Mockito.verify(authenticationManager).authenticate(Mockito.any());
    }
}