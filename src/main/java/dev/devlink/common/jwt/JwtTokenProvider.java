package dev.devlink.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider implements TokenProvider {

    private final Key key;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 24시간

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public JwtToken generateToken(Long memberId) {
        long now = System.currentTimeMillis();
        Date expiryDate = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(new Date(now))
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty.");
        }
        return false;
    }

    @Override
    public Long extractMemberId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }
}
