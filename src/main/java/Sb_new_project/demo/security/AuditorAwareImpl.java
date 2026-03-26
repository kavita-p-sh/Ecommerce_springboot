package Sb_new_project.demo.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getName())) {

                return Optional.of("SYSTEM");
            }

            return Optional.ofNullable(authentication.getName());

        } catch (Exception e) {
            return Optional.of("SYSTEM");
        }
    }
}