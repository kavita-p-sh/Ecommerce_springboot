package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.dto.LoggedInUserDTO;
import Sb_new_project.demo.service.LoggedInUserService;
import Sb_new_project.demo.util.Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggedInUserServiceImpl implements LoggedInUserService {

    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException(Constant.USER_UNAUTHENTICATED);
        }
        return auth;
    }

    @Override
    public LoggedInUserDTO getCurrentUser() {

        Authentication auth = getAuthentication();

        String username = auth.getName();

        List<String> roles = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();

        return new LoggedInUserDTO(username, roles);
    }

    @Override
    public String getUsername() {
        return getAuthentication().getName();
    }

    @Override
    public boolean isAdmin() {
        Authentication auth = getAuthentication();

        return auth.getAuthorities()
                .stream()
                .anyMatch(role -> role.getAuthority().equals(Constant.ROLE_ADMIN));
    }
}