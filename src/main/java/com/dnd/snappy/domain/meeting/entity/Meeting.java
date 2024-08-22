package com.dnd.snappy.domain.meeting.entity;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
import com.dnd.snappy.domain.meeting.dto.request.ModifyMeetingRequestDto;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
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
    private String leaderAuthKey;

    @Column(nullable = false)
    private String meetingLink;

    public static Meeting create(CreateMeetingEntityDto dto) {
        LocalDateTime endDate = Optional.ofNullable(dto.endDate())
                .orElse(dto.startDate().plusDays(1));

        validateStartAndEndDates(dto.startDate(), endDate);

        return Meeting.builder()
                .name(dto.name())
                .startDate(dto.startDate())
                .endDate(endDate)
                .description(dto.description())
                .thumbnailUrl(dto.thumbnailUrl())
                .symbolColor(dto.symbolColor())
                .password(dto.password())
                .leaderAuthKey(dto.leaderAuthKey())
                .meetingLink(dto.meetingLink())
                .build();
    }

    private static void validateStartAndEndDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        Optional.ofNullable(startDate)
                .filter(date -> !date.isBefore(now) && !date.isAfter(now.plusDays(10)))
                .orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST, "시작일은 현재 시간 이후부터 10일 이내여야 합니다."));

        Optional.ofNullable(endDate)
                .filter(date -> date.isAfter(startDate) && !date.isAfter(startDate.plusDays(7)))
                .orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST, "종료일은 시작일 이후여야 하며, 시작일로부터 최대 7일까지만 입력 가능합니다."));
    }

    public MeetingLinkStatus getMeetingLinkStatus() {
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

    public LocalDateTime getExpiredDate() {
        return endDate.plusDays(7);
    }

    public boolean isCorrectPassword(String password) {
        return this.password.equals(password);
    }

    public boolean isCorrectLeaderAuthKey(String leaderAuthKey) {
        return this.leaderAuthKey.equals(leaderAuthKey);
    }

    public boolean canJoinMeeting() {
        MeetingLinkStatus currStatus = getMeetingLinkStatus();
        return currStatus == MeetingLinkStatus.PENDING || currStatus == MeetingLinkStatus.IN_PROGRESS;
    }

    public void validateCanShoot() {
        if(getMeetingLinkStatus() != MeetingLinkStatus.IN_PROGRESS) {
            throw new BusinessException(MeetingErrorCode.NO_IN_PROGRESS_MEETING);
        }
    }

    public void modifyMeeting(ModifyMeetingRequestDto dto) {
        if (dto.name() != null) {
            this.name = dto.name();
        }
        if (dto.description() != null) {
            this.description = dto.description();
        }
        if (dto.symbolColor() != null) {
            this.symbolColor = dto.symbolColor();
        }
    }

}