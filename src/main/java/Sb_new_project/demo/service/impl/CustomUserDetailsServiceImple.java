package Sb_new_project.demo.service.impl;

import Sb_new_project.demo.entity.User;
import Sb_new_project.demo.repository.UserRepository;
import Sb_new_project.demo.service.AdminService;
import Sb_new_project.demo.service.CustomUserDetailsService;
import Sb_new_project.demo.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsServiceImple implements CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Load user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException(Constant.USER_NOT_FOUND);
                });

        log.info("User: {} Role: {}", user.getUsername(), user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
               List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()))
        );
    }


}