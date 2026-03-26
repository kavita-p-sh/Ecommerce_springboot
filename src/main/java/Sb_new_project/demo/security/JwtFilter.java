    package Sb_new_project.demo.security;

    import Sb_new_project.demo.service.impl.CustomUserDetailsServiceImple;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @Slf4j
    public class JwtFilter extends OncePerRequestFilter {

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private CustomUserDetailsServiceImple userDetailsService;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            String path = request.getServletPath();

            log.info("Request path: {}", path);

            if (path.equals("/auth/login") ||
                    path.equals("/auth/register-user") ||
                    path.equals("/auth/register-admin")) {

                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                log.warn("Authorization header missing or invalid");
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);

            try {

                String username = jwtUtil.extractUsername(token);


                if (username != null ) {

                    var userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.info("User authenticated successfully: {}", username);
                    }
                }

            } catch (Exception e) {

                log.error("JWT validation failed: {}", e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }

