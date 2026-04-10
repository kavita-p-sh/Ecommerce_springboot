package com.ecommerce.api.config;

import com.ecommerce.api.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j

public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Gets current user for auditing
     */
    @Override
    public Optional<String> getCurrentAuditor() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                return Optional.of(AppConstants.SYSTEM);
            }

            if (!authentication.isAuthenticated()) {
                return Optional.of(AppConstants.SYSTEM);
            }

            if ("anonymousUser".equals(authentication.getName())) {
                return Optional.of(AppConstants.SYSTEM);
            }

            return Optional.of(authentication.getName());

        } catch (Exception e) {
            log.warn("Could not determine current auditor, defaulting to SYSTEM", e);
            return Optional.of(AppConstants.SYSTEM);
        }
    }
}
