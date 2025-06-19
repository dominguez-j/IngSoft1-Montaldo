package ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final String secret;
    private final Long expiration;

    @Autowired
    JwtService(
            @Value("${jwt.access.secret}") String secret,
            @Value("${jwt.access.expiration}") Long expiration
    ) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String createToken(JwtUserDetails claims) {
        return Jwts.builder()
                .subject(claims.email())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .claim("role", claims.role())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String createEmailVerificationToken(String email, long expirationMillis) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .claim("type", "email_verification") // permite distinguir usos
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String createPasswordResetToken(String email, long expirationMillis) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "password_reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    Optional<JwtUserDetails> extractVerifiedUserDetails(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (claims.containsKey("sub")
                    && claims.containsKey("role")
                    && claims.get("role") instanceof String role
            ) {
                return Optional.of(new JwtUserDetails(claims.getSubject(), role));
            }
        } catch (Exception e) {
            // Some exception happened during jwt parse
        }
        return Optional.empty();
    }

    public Optional<String> extractEmailFromVerificationToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if ("email_verification".equals(claims.get("type"))) {
                return Optional.of(claims.getSubject());
            }
        } catch (Exception e) {
            // Token inválido o expirado
        }
        return Optional.empty();
    }

    public Optional<String> extractEmailFromPasswordResetToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if ("password_reset".equals(claims.get("type"))) {
                return Optional.of(claims.getSubject());
            }
        } catch (Exception e) {
            // Token inválido o expirado
        }
        return Optional.empty();
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }
}
