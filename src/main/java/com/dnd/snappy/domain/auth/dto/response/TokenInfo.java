package com.dnd.snappy.domain.auth.dto.response;

public record TokenInfo(
        String token,
        Long payload
) {
}
