package com.dnd.snappy.domain.mission.entity;

import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionEntityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Mission extends BaseEntity {
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    public static Mission create(CreateMissionEntityDto dto, Meeting meeting) {
        return Mission.builder()
                .content(dto.content())
                .meeting(meeting)
                .build();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean belongsToMeeting(Long meetingId) {
        return this.meeting.getId().equals(meetingId);
    }
}

