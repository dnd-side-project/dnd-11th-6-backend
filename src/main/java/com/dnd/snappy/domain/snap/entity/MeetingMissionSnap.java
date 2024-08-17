package com.dnd.snappy.domain.snap.entity;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.participant.entity.Participant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Meeting_MISSION_SNAP")
public class MeetingMissionSnap extends Snap {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    public MeetingMissionSnap(String snapUrl, LocalDateTime shootDate, Meeting meeting, Participant participant, Mission mission) {
        super(snapUrl, shootDate, meeting, participant);
        this.mission = mission;
    }

    public static MeetingMissionSnap create(String snapUrl, LocalDateTime shootDate, Meeting meeting, Participant participant, Mission mission) {
        meeting.validateCanShoot();
        participant.addShootCount();
        return new MeetingMissionSnap(snapUrl, shootDate, meeting, participant, mission);
    }
}
