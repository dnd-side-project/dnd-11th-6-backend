package com.dnd.snappy.domain.mission.dto.response;

public record LeaderMeetingMissionDetailResponseDto(
        Long missionId,
        String content,
        boolean hasParticipants
) {
}
