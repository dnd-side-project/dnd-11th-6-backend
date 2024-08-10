package com.dnd.snappy.controller.v1.member.response;

public record ParticipationResponse(
        Long memberId,
        String accessToken
) {
}
