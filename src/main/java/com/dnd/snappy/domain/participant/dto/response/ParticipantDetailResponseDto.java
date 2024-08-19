package com.dnd.snappy.domain.participant.dto.response;

import com.dnd.snappy.domain.participant.entity.Role;

public record ParticipantDetailResponseDto(
        Long participantId,
        String nickname,
        Role role,
        Integer shootCount
) {
}
