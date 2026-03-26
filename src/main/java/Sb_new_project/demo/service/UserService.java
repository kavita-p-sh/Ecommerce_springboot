package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.RegisterRequestDTO;
import Sb_new_project.demo.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    @Transactional
    User registerUserAsUser(RegisterRequestDTO dto);

    @Transactional
    User registerUserAsAdmin(RegisterRequestDTO dto);

    boolean adminExists();

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    @Transactional
    User updateUser(Long id, RegisterRequestDTO dto);

    @Transactional
    User updateUserByUsername(String username, RegisterRequestDTO dto);

    @Transactional
    void deleteUser(Long id);
}
