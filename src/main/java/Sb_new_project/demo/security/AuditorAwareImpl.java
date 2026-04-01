package Sb_new_project.demo.security;

import Sb_new_project.demo.util.Constant;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                return Optional.of(Constant.SYSTEM);
            }

            if (!authentication.isAuthenticated()) {
                return Optional.of(Constant.SYSTEM);
            }
            return Optional.of(authentication.getName());

        } catch (Exception e) {
            return Optional.of(Constant.SYSTEM);
        }
    }
}