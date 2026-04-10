package com.ecommerce.api.config;

import com.ecommerce.api.audit.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


/**
 * Configuration class for JPA auditing.
 * Enables automatic tracking of createdBy and updatedBy fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    /**
     *
     * Provides current auditor (current-logged in user).
     *
     * @return AuditorAware implementation
     */
    @Bean
    public AuditorAware<String> auditorProvider()
    {
        return new AuditorAwareImpl();
    }
}