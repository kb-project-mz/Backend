package fingertips.backend.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProcessor {

    private static final long ACCESS_TOKEN_VALID_MILLISECONDS = 1000L * 60 * 60 * 24 * 4; // 24시간
    private static final long REFRESH_TOKEN_VALID_MILLISECONDS = 1000L * 60 * 60 * 24 * 30; // 30 일

    @Value("${jwt.secretKey}")
    private String secretKey;

    private Key key;

    @PostConstruct
    private void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Secret key must not be null or empty");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String subject, Integer memberIdx, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("memberIdx", memberIdx)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_VALID_MILLISECONDS))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + REFRESH_TOKEN_VALID_MILLISECONDS))
                .signWith(key)
                .compact();
    }

    public String getMemberId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Integer getMemberIdx(String token) {
        String accessToken = token.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        return claims.get("memberIdx", Integer.class);
    }

    public String getUserRole(String token) {
        String accessToken = token.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdmin(String token) {
        String role = getUserRole(token);
        return "ROLE_ADMIN".equals(role);
    }
}
