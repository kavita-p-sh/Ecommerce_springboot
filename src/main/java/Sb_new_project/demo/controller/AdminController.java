package Sb_new_project.demo.controller;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import Sb_new_project.demo.service.AdminService;
import Sb_new_project.demo.util.Constant;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/active-users")
    @RolesAllowed(Constant.ROLE_ADMIN)
    public ResponseEntity<List<LoggedInUserDTO>> getActiveUsers() {
        return ResponseEntity.ok(adminService.getActiveUsers());
    }
}