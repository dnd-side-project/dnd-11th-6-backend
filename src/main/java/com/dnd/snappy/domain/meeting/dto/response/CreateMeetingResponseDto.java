package com.dnd.snappy.domain.meeting.dto.response;

import com.dnd.snappy.domain.meeting.entity.MeetingLinkStatus;

public record CreateMeetingResponseDto(
        String meetingLink,
        String password,
        MeetingLinkStatus status
) {}
