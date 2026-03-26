package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.LoggedInUserDTO;

public interface LoggedInUserService {
    LoggedInUserDTO getCurrentUser();

    String getUsername();

    boolean isAdmin();
}
