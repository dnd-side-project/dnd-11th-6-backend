package com.dnd.snappy.domain.meeting.dto.request;

import java.time.LocalDateTime;

public record CreateMeetingEntityDto(
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String description,
        String thumbnailUrl,
        String symbolColor,
        String password,
        String meetingLink,
        String leaderAuthKey
) {
    public static CreateMeetingEntityDto of(CreateMeetingRequestDto requestDto, String thumbnailUrl, String meetingLink, String leaderAuthKey) {
        return new CreateMeetingEntityDto(
                requestDto.name(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                thumbnailUrl,
                requestDto.symbolColor(),
                requestDto.password(),
                meetingLink,
                leaderAuthKey
        );
    }
}
