package com.dnd.snappy.domain.auth.service;

import static com.dnd.snappy.domain.auth.exception.AuthErrorCode.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenExtractor {

    private final PathVariableExtractor pathVariableExtractor;
    private final AuthCookieManager authCookieManager;

    public String extractToken(final HttpServletRequest request, TokenType tokenType) {
        final Long meetingId = pathVariableExtractor.extractMeetingId(request);
        Cookie cookie = authCookieManager.getAuthCookie(request, tokenType, meetingId)
                .orElseThrow(() -> new BusinessException(UNAUTHORIZED));

        return cookie.getValue();
    }
}
