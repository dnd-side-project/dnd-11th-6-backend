package com.dnd.snappy.controller.v1.auth;

import org.springframework.stereotype.Component;

@Component
public class AuthTokenCookieNameGenerator {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String TOKEN_DELIMITER = "_";

    public String generateAccessToken(Long meetingId) {
        return new StringBuilder()
                .append(ACCESS_TOKEN)
                .append(TOKEN_DELIMITER)
                .append(meetingId)
                .toString();
    }

    public String generateRefreshToken(Long meetingId) {
        return new StringBuilder()
                .append(REFRESH_TOKEN)
                .append(TOKEN_DELIMITER)
                .append(meetingId)
                .toString();
    }
}
