package com.dnd.snappy.domain.snap.entity;

import com.dnd.snappy.domain.meeting.entity.Meeting;
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
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("RANDOM_MISSION")
public class RandomMissionSnap extends Snap {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "random_mission_id", nullable = false)
    private RandomMission randomMission;

    private RandomMissionSnap(String snapUrl, LocalDateTime shootDate, Meeting meeting, Participant participant, RandomMission randomMission) {
        super(snapUrl, shootDate, meeting, participant);
        this.randomMission = randomMission;
    }

    public static RandomMissionSnap create(String snapUrl, LocalDateTime shootDate, Meeting meeting, Participant participant, RandomMission randomMission) {
        meeting.validateCanShoot();
        participant.addShootCount();
        return new RandomMissionSnap(snapUrl, shootDate, meeting, participant, randomMission);
    }

    public Integer getMissionId() {
        return randomMission.getId();
    }

    public String getMissionContent() {
        return randomMission.getContent();
    }
}
