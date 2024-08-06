package com.dnd.snappy.domain.meeting.entity;

import java.time.LocalDateTime;

public enum MeetingLinkStatus {
    INACTIVE, // 비활성화 (시작일 전)
    ACTIVE,   // 활성화 (시작일 ~ 종료일 + 7일)
    INVALID;  // 유효하지 않음 (종료일 + 7일이 지난 후)

    public static MeetingLinkStatus checkLinkStatus(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) {
        if (startDate.isAfter(now)) {
            return INACTIVE;
        }

        LocalDateTime defaultEndDate = (endDate != null) ? endDate : startDate.plusHours(24);
        LocalDateTime validUntil = defaultEndDate.plusDays(7);

        if (!now.isAfter(validUntil)) {
            return ACTIVE;
        }

        return INVALID;
    }
}

