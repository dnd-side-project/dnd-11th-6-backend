package com.dnd.snappy.domain.meeting.dto.request;

import java.time.LocalDateTime;
import java.util.Optional;

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
                Optional.ofNullable(requestDto.endDate()).orElse(requestDto.startDate().plusDays(1)),
                requestDto.description(),
                thumbnailUrl,
                requestDto.symbolColor(),
                requestDto.password(),
                leaderAuthKey,
                meetingLink
        );
    }
}
