package com.dnd.snappy.controller.v1.auth.resolver;

public record ReissueAuthInfo(
        Long participantId,
        String refreshToken
) {
}
