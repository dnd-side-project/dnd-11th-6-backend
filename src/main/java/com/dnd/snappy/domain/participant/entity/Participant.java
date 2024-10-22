package com.dnd.snappy.domain.participant.entity;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
public class Participant extends BaseEntity {

    public static final int DEFAULT_SHOOT_COUNT = 0;
    public static final int MAX_SHOOT_COUNT = 10;

    @Column(nullable = false, length = 8)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Integer shootCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    public static Participant create(String nickname, Role role, Meeting meeting) {
        return Participant.builder()
                .nickname(nickname)
                .role(role)
                .meeting(meeting)
                .shootCount(DEFAULT_SHOOT_COUNT)
                .build();
    }

    public boolean isLeader(Long meetingId) {
        return this.role == Role.LEADER && this.meeting.getId().equals(meetingId);
    }

    public void addShootCount() {
        if(canNotShoot()) {
            throw new BusinessException(ParticipantErrorCode.EXCEED_MAX_SHOOT_COUNT);
        }
        shootCount++;
    }

    private boolean canNotShoot() {
        return this.shootCount == MAX_SHOOT_COUNT;
    }
}
