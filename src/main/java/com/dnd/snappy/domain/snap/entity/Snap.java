package com.dnd.snappy.domain.snap.entity;

import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.mission.entity.Mission;
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
public class Snap extends BaseEntity {

    @Column(name = "snap_url")
    private String snapUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "random_mission_id")
    private RandomMission randomMission;
}
