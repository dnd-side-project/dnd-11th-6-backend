package com.dnd.snappy.controller.v1.auth;

import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieManager {

    private static final String SAME_SITE_OPTION = "None";

    private final AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    public AuthCookieManager(AuthTokenCookieNameGenerator authTokenCookieNameGenerator) {
        this.authTokenCookieNameGenerator = authTokenCookieNameGenerator;
    }

    public String createAccessTokenCookie(String value, Long meetingId, String path, Duration duration) {
        return createCookie(authTokenCookieNameGenerator.generateAccessToken(meetingId), value, path, duration);
    }

    public String createRefreshTokenCookie(String value, Long meetingId, String path, Duration duration) {
        return createCookie(authTokenCookieNameGenerator.generateRefreshToken(meetingId), value, path, duration);
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
