package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import Sb_new_project.demo.service.ActiveUsersStore;
import Sb_new_project.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ActiveUsersStore activeUsersStore;

    @Override
    public List<LoggedInUserDTO> getActiveUsers() {

        return activeUsersStore.getAllUsers()
                .values()
                .stream()
                .toList();
    }
}