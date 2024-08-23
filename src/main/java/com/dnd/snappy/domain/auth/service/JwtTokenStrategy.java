package com.dnd.snappy.domain.auth.service;

import com.dnd.snappy.domain.auth.dto.response.TokenInfo;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenStrategy {

    private final JwtTokenExtractor jwtTokenExtractor;
    private final TokenProvider tokenProvider;

    public TokenInfo process(HttpServletRequest request, TokenType tokenType) {
        String token = jwtTokenExtractor.extractToken(request, tokenType);
        Long payload = tokenProvider.extractPayload(token);
        return new TokenInfo(token, payload);
    }

    public TokenInfo loginProcess(HttpServletRequest request) {
        String token = jwtTokenExtractor.extractToken(request, TokenType.ACCESS_TOKEN);
        Long payload = tokenProvider.extractPayloadIgnoringExpiration(token);
        return new TokenInfo(token, payload);
    }
}
