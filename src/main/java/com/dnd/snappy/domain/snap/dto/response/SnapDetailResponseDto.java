package com.dnd.snappy.domain.snap.dto.response;

import java.time.LocalDateTime;

public record SnapDetailResponseDto(
        Long snapId,
        String snapUrl,
        LocalDateTime shootDate,
        String type,
        ParticipantDetailResponseDto photographer,
        MissionDetailResponseDto mission
) {
}
