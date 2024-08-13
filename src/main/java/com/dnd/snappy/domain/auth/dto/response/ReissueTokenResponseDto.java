package com.dnd.snappy.domain.auth.dto.response;

import java.time.LocalDateTime;

public record ReissueTokenResponseDto(
        String accessToken,
        String refreshToken,
        LocalDateTime meetingExpiredDate
) {
}
