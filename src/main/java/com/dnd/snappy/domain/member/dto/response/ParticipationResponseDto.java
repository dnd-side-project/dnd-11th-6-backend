package com.dnd.snappy.domain.member.dto.response;

public record ParticipationResponseDto(
        Long memberId,
        String accessToken,
        String refreshToken
) {
}
