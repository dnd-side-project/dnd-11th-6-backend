package com.dnd.snappy.domain.mission.dto.request;

public record CreateMissionEntityDto(
        String content,
        Long meetingId
) {
    public static CreateMissionEntityDto from(CreateMissionRequestDto requestDto, Long meetingId) {
        return new CreateMissionEntityDto(
                requestDto.content(),
                meetingId
        );
    }
}
