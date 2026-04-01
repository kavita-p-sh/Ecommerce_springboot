package Sb_new_project.demo.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

        @Value("${jwt.secret}")
        private String secret;

        @Value("${jwt.expiration}")
        private long expiration;

        public String generateToken(UserDetails userDetails) {

            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();
        }

        public String extractUsername(String token) {

            token = getToken(token);

            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

        public String extractRole(String token) {

            token = getToken(token);

            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        }

        public boolean validateToken(String token, UserDetails userDetails) {

            try {
                String username = extractUsername(token);

                return username.equals(userDetails.getUsername())
                        && !isTokenExpired(token);

            } catch (Exception e) {
                return false;
            }
        }

        private boolean isTokenExpired(String token) {

            token = getToken(token);

            Date expDate = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expDate.before(new Date());
        }

        private String getToken(String token) {
            return token.replace("Bearer ", "");
        }
    }