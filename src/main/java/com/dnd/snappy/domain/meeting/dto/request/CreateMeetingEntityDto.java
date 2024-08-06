package com.dnd.snappy.domain.meeting.dto.request;

import com.dnd.snappy.domain.meeting.entity.Meeting;

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

    public Meeting toEntity() {
        LocalDateTime defaultEndDate = endDate != null ? endDate : startDate.plusHours(24);
        return Meeting.builder()
                .name(name)
                .startDate(startDate)
                .endDate(defaultEndDate)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .symbolColor(symbolColor)
                .password(password)
                .adminPassword(adminPassword)
                .meetingLink(meetingLink)
                .build();
    }
}
