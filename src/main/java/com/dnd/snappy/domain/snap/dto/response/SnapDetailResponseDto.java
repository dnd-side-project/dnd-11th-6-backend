package com.dnd.snappy.domain.snap.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

public record SnapDetailResponseDto(
        Long snapId,
        String snapUrl,
        LocalDateTime shootDate,
        String type,
        ParticipantDetailResponseDto photographer,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        MissionDetailResponseDto mission
) {
}
