package com.dnd.snappy.domain.meeting.dto.response;

import com.dnd.snappy.domain.meeting.entity.Meeting;

import java.time.LocalDateTime;

public record CreateMeetingResponseDto(
        String name,
        String meetingLink,
        String leaderAuthKey,
        String password,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public CreateMeetingResponseDto(Meeting meeting) {
        this(
                meeting.getName(),
                meeting.getMeetingLink(),
                meeting.getLeaderAuthKey(),
                meeting.getPassword(),
                meeting.getStartDate(),
                meeting.getEndDate());
    }
}
