package com.dnd.snappy.domain.meeting.dto.response;

public record ModifyMeetingResponseDto(
        Long meetingId,
        String name,
        String description,
        String symbolColor
) {
}
