package com.dnd.snappy.domain.token.dto;

public record Tokens(
        String accessToken,
        String refreshToken
) {
}
