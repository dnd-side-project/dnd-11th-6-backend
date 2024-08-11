package com.dnd.snappy.domain.participant.dto.response;

public record ParticipationResponseDto(
        Long participantId,
        String accessToken,
        String refreshToken
) {
}
