package Sb_new_project.demo.security;

import Sb_new_project.demo.exception.UserNotFoundException;
import Sb_new_project.demo.util.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(SecretKey secretKey, @Value("${jwt.expiration}") long expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(object -> object.toString())
                .orElseThrow(() -> new UserNotFoundException(Constant.USER_NOT_FOUND));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .encryptWith(secretKey, Jwts.KEY.DIRECT, Jwts.ENC.A256GCM)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(getToken(token));
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(getToken(token));
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(getToken(token));
        return claims.getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .decryptWith(secretKey)
                .build()
                .parseEncryptedClaims(token)
                .getPayload();
    }

    private String getToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}