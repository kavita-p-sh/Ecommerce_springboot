package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveUsersStore {

    private final Map<String, LoggedInUserDTO> activeUsers = new ConcurrentHashMap<>();

    public void addUser(String token, LoggedInUserDTO user) {
        activeUsers.put(token, user);
    }

    public void removeUser(String token) {
        activeUsers.remove(token);
    }

    public Map<String, LoggedInUserDTO> getAllUsers() {
        return activeUsers;
    }
}