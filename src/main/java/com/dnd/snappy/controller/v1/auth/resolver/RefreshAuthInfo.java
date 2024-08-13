package com.dnd.snappy.controller.v1.auth.resolver;

public record RefreshAuthInfo(
        Long participantId,
        String refreshToken
) {
}
