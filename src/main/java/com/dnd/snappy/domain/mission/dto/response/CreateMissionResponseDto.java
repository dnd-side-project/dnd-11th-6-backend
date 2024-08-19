package com.dnd.snappy.domain.mission.dto.response;

public record CreateMissionResponseDto(
        Long missionId,
        String content
) { }
