package com.dnd.snappy.domain.participant.dto.response;

import com.dnd.snappy.domain.participant.entity.Role;

public record ParticipantResponseDto(
        Long participantId,
        Role role,
        String nickname,
        Integer shootCount
) {
}
