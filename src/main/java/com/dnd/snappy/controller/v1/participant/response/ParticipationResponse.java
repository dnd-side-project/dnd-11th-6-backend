package com.dnd.snappy.controller.v1.participant.response;

public record ParticipationResponse(
        Long participantId,
        String accessToken
) {
}
