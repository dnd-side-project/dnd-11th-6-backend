package com.dnd.snappy.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieManager {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String SAME_SITE_OPTION = "None";
    private static final long REFRESH_TOKEN_COOKIE_AGE = 3600L;

    public String createNewCookie(String value, String path) {
        return createCookie(value, path, REFRESH_TOKEN_COOKIE_AGE);
    }

    private String createCookie(String value, String path, long maxAge) {
        return ResponseCookie.from(REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .sameSite(SAME_SITE_OPTION)
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
