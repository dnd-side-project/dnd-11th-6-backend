package com.dnd.snappy.controller.v1.auth;

import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieManager {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String SAME_SITE_OPTION = "None";

    public String createAccessTokenCookie(String value, Long meetingId, String path, Duration duration) {
        return createAuthTokenCookie(ACCESS_TOKEN, meetingId, value, path, duration);
    }

    public String createRefreshTokenCookie(String value, Long meetingId, String path, Duration duration) {
        return createAuthTokenCookie(REFRESH_TOKEN, meetingId, value, path, duration);
    }

    private String createAuthTokenCookie(String namePrefix, Long meetingId, String value, String path, Duration duration) {
        String name = generateCookieName(namePrefix, meetingId);
        return createCookie(name, value, path, duration);
    }

    private String generateCookieName(String value, Long meetingId) {
        return value + "_" + meetingId;
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
}
