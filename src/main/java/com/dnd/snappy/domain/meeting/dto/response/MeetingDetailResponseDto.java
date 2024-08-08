package com.dnd.snappy.domain.meeting.dto.response;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.entity.MeetingLinkStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record MeetingDetailResponseDto(
        Long meetingId,
        String name,
        String description,
        String thumbnailUrl,
        String symbolColor,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime startDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime endDate,
        MeetingLinkStatus status
) {

    public MeetingDetailResponseDto(Meeting meeting) {
        this(
                meeting.getId(),
                meeting.getName(),
                meeting.getDescription(),
                meeting.getThumbnailUrl(),
                meeting.getSymbolColor(),
                meeting.getStartDate(),
                meeting.getEndDate(),
                meeting.getMeetingLinkStatus()
        );
    }
}

