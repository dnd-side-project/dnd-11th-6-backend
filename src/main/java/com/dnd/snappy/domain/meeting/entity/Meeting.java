package com.dnd.snappy.domain.meeting.entity;

import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class Meeting extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private String description;

    @Column
    private String thumbnailUrl;

    @Column(nullable = false)
    private String symbolColor;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String adminPassword;

    @Column(nullable = false)
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingLinkStatus status;

    public static Meeting toEntity(CreateMeetingRequestDto requestDto, String meetingLink, MeetingLinkStatus status) {
        // endDate 입력안하면 startDate에 24시간을 더한 값을 기본값으로 설정
        LocalDateTime endDate = requestDto.endDate() != null ? requestDto.endDate() : requestDto.startDate().plusHours(24);

        return Meeting.builder()
                .name(requestDto.name())
                .description(requestDto.description())
                .symbolColor(requestDto.symbolColor())
                .startDate(requestDto.startDate())
                .endDate(endDate)
                .password(requestDto.password())
                .adminPassword(requestDto.adminPassword())
                .meetingLink(meetingLink)
                .status(status)
                .build();
    }

}



