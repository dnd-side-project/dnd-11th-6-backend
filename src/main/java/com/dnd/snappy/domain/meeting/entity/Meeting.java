package com.dnd.snappy.domain.meeting.entity;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Optional;

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

    @Column(nullable = false)
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

    public static Meeting create(CreateMeetingEntityDto dto) {
        LocalDateTime endDate = dto.endDate() != null ? dto.endDate() : dto.startDate().plusDays(1);
        validateStartAndEndDates(dto.startDate(), dto.endDate());

        return Meeting.builder()
                .name(dto.name())
                .startDate(dto.startDate())
                .endDate(endDate)
                .description(dto.description())
                .thumbnailUrl(dto.thumbnailUrl())
                .symbolColor(dto.symbolColor())
                .password(dto.password())
                .adminPassword(dto.adminPassword())
                .meetingLink(dto.meetingLink())
                .build();
    }

    private static void validateStartAndEndDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysLater = now.plusDays(10);

        Optional.ofNullable(startDate)
                .filter(date -> !date.isBefore(now) && !date.isAfter(tenDaysLater))
                .orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST, "시작일은 현재 시간 이후부터 10일 이내여야 합니다."));

        if (endDate != null && !endDate.isAfter(startDate)) {
            throw new BusinessException(CommonErrorCode.BAD_REQUEST, "종료일은 시작일 이후여야 합니다.");
        }
    }

    public MeetingLinkStatus getMeetingLinkStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            return MeetingLinkStatus.PENDING;
        } else if (now.isAfter(endDate) && now.isBefore(endDate.plusDays(7))) {
            return MeetingLinkStatus.COMPLETED;
        } else if (now.isAfter(endDate.plusDays(7))) {
            return MeetingLinkStatus.EXPIRED;
        } else {
            return MeetingLinkStatus.IN_PROGRESS;
        }
    }

}
