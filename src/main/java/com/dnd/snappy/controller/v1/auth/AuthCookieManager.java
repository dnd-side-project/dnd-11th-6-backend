package com.dnd.snappy.controller.v1.auth;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCookieManager {

    private static final String PATH = "/api/";
    private static final String SAME_SITE_OPTION = "None";

    private final AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    public String createTokenCookie(TokenType tokenType, String value, Long meetingId, Duration duration) {
        return createCookie(authTokenCookieNameGenerator.generateCookieName(tokenType, meetingId), value, PATH, duration);
    }

    private String createCookie(String name, String value, String path, Duration duration) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .sameSite(SAME_SITE_OPTION)
                .maxAge(duration)
                .build()
                .toString();
    }

    public Optional<Cookie> getAuthCookie(HttpServletRequest request, TokenType tokenType, Long meetingId) {
        final String cookieName = authTokenCookieNameGenerator.generateCookieName(tokenType, meetingId);

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst();
    }
}
