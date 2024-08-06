package com.dnd.snappy.domain.meeting.entity;

import java.time.LocalDateTime;

public enum MeetingLinkStatus {
    INACTIVE, // 비활성화 (시작일 전)
    ACTIVE,   // 활성화 (시작일 ~ 종료일 + 7일)
    INVALID;  // 유효하지 않음 (종료일 + 7일이 지난 후)

    // TODO: 모임 링크 및 코드 입력 API에서 상태 업데이트 로직 구현
    // TODO: status=INVALID 일 때, 배치 작업을 통해 모임 삭제 구현
    public static MeetingLinkStatus calculateStatus(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) {
        if (startDate.isAfter(now)) {
            return INACTIVE;
        }

        LocalDateTime validUntil = endDate.plusDays(7);
        if (!now.isAfter(validUntil)) {
            return ACTIVE;
        }

        return INVALID;
    }

}
