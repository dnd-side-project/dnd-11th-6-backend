package com.dnd.snappy.domain.participant.dto.response;

import java.time.LocalDateTime;

public record CreateParticipantResponseDto(
        Long participantId,
        LocalDateTime meetingExpiredDate
) {
}
