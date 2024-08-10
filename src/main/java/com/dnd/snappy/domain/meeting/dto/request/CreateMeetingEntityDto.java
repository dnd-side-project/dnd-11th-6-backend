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
        String adminPassword,
        String meetingLink
) {
    public static CreateMeetingEntityDto of(CreateMeetingRequestDto requestDto, String thumbnailUrl, String meetingLink) {
        return new CreateMeetingEntityDto(
                requestDto.name(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                thumbnailUrl,
                requestDto.symbolColor(),
                requestDto.password(),
                requestDto.adminPassword(),
                meetingLink
        );
    }
}

