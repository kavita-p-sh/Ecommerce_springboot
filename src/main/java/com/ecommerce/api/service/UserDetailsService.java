package com.ecommerce.api.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Custom service to load user details from database for authentication.
 */
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {
    /**
     * Fetches user details by username during login.
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
