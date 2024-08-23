package com.dnd.snappy.domain.snap.dto.response;

public record MissionDetailResponseDto(
        Long missionId,
        String content
) {
    public MissionDetailResponseDto(Integer missionId, String content) {
        this(Long.valueOf(missionId), content);
    }
}
