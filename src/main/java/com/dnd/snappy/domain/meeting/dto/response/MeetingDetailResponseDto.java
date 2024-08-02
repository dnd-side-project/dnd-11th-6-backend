package com.dnd.snappy.domain.meeting.dto.response;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import java.time.LocalDateTime;

public record MeetingDetailResponseDto(
        Long meetingId,
        String name,
        String description,
        String thumbnailUrl,
        String symbolColor,
        LocalDateTime startDate,
        LocalDateTime endDate
) {

    public MeetingDetailResponseDto(Meeting meeting) {
        this(
                meeting.getId(),
                meeting.getName(),
                meeting.getDescription(),
                meeting.getThumbnailUrl(),
                meeting.getSymbolColor(),
                meeting.getStartDate(),
                meeting.getEndDate()
        );
    }
}
