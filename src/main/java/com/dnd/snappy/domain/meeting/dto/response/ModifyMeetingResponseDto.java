package com.dnd.snappy.domain.meeting.dto.response;

public record ModifyMeetingResponseDto(
        Long MeetingId,
        String name,
        String description,
        String symbolColor
) {
}
