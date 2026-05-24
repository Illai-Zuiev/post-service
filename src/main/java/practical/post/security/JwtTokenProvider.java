package practical.post.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import practical.post.model.constants.AuthConstants;
import practical.post.model.entity.Role;
import practical.post.model.entity.User;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expirationTime) {
        this.secretKey = getSecretKey(secretKey);
        this.expirationTime = expirationTime;
    }

    private SecretKey getSecretKey(String secretKey) {
        byte[] decode64 = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decode64);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(AuthConstants.USER_ID, user.getId());
        claims.put(AuthConstants.USERNAME, user.getUsername());
        claims.put(AuthConstants.USER_REGISTRATION_STATUS, user.getRegistrationStatus().name());
        claims.put(AuthConstants.EMAIL, user.getEmail());
        claims.put(AuthConstants.LAST_LOGIN, LocalDateTime.now().toString());
        claims.put(AuthConstants.ROLES, user.getRoles().stream().map(Role::getName).toList());

        return createToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String refreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return createToken(claims, claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = getAllClaimsFromToken(token);

        return claims.get(AuthConstants.ROLES, List.class);
    }
}
