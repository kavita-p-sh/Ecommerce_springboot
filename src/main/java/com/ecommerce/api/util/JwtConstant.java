package com.ecommerce.api.util;

import java.util.List;

/**
 * Utility class that contains jwt related constants
 * Security path related constants
 */

public class JwtConstant {

    private JwtConstant() {}

    public static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register"

    );

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String BEARER_AUTH = "bearerAuth";
    public static final String SCHEME_BEARER = "bearer";
    public static final String BEARER_FORMAT = "JWT";


    public static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";

    public static final List<String> PERMIT_ALL_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/otp/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

}

