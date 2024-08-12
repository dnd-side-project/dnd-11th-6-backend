package com.dnd.snappy.domain.participant.dto.response;

import java.time.LocalDateTime;

public record ParticipationResponseDto(
        Long participantId,
        LocalDateTime meetingExpiredDate,
        String accessToken,
        String refreshToken
) {
}
