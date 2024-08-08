package com.dnd.snappy.domain.meeting.entity;

public enum MeetingLinkStatus {
    PENDING,      // 대기중: 시작일 전
    IN_PROGRESS,  // 진행중: 시작일 ~ 종료일
    COMPLETED,    // 종료: 종료일 ~ +7일
    EXPIRED;      // 만료: 종료일 +7일 후
}
