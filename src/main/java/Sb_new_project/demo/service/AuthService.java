package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.LoginRequestDTO;
import Sb_new_project.demo.dto.LoginResponseDTO;
import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.dto.UserResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO dto);

    UserResponseDTO register(RegisterRequestDTO dto);
}