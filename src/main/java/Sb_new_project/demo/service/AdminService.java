package Sb_new_project.demo.service;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import java.util.List;

public interface AdminService {
    List<LoggedInUserDTO> getActiveUsers();
}