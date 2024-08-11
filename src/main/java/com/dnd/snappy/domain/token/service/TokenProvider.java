package com.dnd.snappy.domain.token.service;

import static com.dnd.snappy.domain.token.service.TokenType.*;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.token.dto.Tokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String PARTICIPANT_ID = "participantId";

    private final JwtProperties jwtProperties;

    public Tokens issueTokens(Long participantId) {
        return new Tokens(
                generateToken(participantId, jwtProperties.getAccessTokenExpireTime()),
                generateToken(participantId, jwtProperties.getRefreshTokenExpireTime())
        );
    }

    public String issueToken(Long participantId, TokenType token) {
        Long expireTime = token == ACCESS_TOKEN ? jwtProperties.getAccessTokenExpireTime() : jwtProperties.getRefreshTokenExpireTime();
        return generateToken(
                participantId,
                expireTime
        );
    }

    private String generateToken(Long participantId, Long expireTime) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expireTime);
        return Jwts.builder()
                .claim(PARTICIPANT_ID, participantId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(jwtProperties.getSecretKey(), SIG.HS256)
                .compact();
    }

    public Long extractPayload(String token) {
        Claims claims = extractClaims(token);
        return claims.get(PARTICIPANT_ID, Long.class);
    }

    public Long extractPayloadIgnoringExpiration(String token) {
        Claims claims = extractClaimsIgnoringExpiration(token);
        return claims.get(PARTICIPANT_ID, Long.class);
    }

    private Claims extractClaims(String token) {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload();
        }
        catch (ExpiredJwtException e) {
            log.warn("[TokenProvider - ExpiredJwtException] {} ", e.getMessage());
            throw new BusinessException(CommonErrorCode.JWT_EXPIRED_ERROR);
        }
        catch (JwtException | IllegalArgumentException e) {
            log.warn("[TokenProvider - JwtException or IllegalArgumentException] {} ", e.getMessage());
            throw new BusinessException(CommonErrorCode.JWT_EXTRACT_ERROR);
        }
    }

    private Claims extractClaimsIgnoringExpiration(String token) {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload();
        }
        catch (ExpiredJwtException e) {
            return e.getClaims();
        }
        catch (JwtException | IllegalArgumentException e) {
            log.warn("[TokenProvider - JwtException or IllegalArgumentException] {} ", e.getMessage());
            throw new BusinessException(CommonErrorCode.JWT_EXTRACT_ERROR);
        }
    }

    private JwtParser getJwtParser() {
        return Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build();
    }
}
