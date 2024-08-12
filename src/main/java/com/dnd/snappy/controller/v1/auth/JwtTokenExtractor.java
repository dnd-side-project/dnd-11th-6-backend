package com.dnd.snappy.controller.v1.auth;


import static com.dnd.snappy.domain.token.exception.TokenErrorCode.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class JwtTokenExtractor {

    private final AuthCookieManager authCookieManager;

    public String extractToken(final HttpServletRequest request, Long meetingId, TokenType tokenType) {
        Cookie cookie = authCookieManager.getCookie(request, tokenType, meetingId);
        if(cookie == null) {
            throw new BusinessException(JWT_EXTRACT_ERROR);
        }

        return cookie.getValue();
    }
}
