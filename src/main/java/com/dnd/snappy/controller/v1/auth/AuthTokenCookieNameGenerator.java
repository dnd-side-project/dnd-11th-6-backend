package com.dnd.snappy.controller.v1.auth;

import com.dnd.snappy.domain.token.service.TokenType;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenCookieNameGenerator {

    private static final String TOKEN_DELIMITER = "_";

    public String generateCookieName(TokenType tokenType, Long meetingId) {
        return new StringBuilder()
                .append(tokenType.getValue())
                .append(TOKEN_DELIMITER)
                .append(meetingId)
                .toString();
    }
}
