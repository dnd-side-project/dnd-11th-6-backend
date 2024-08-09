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
        String leaderAuthKey,
        String meetingLink
) { }
