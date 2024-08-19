package com.dnd.snappy.domain.mission.entity;

import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.participant.entity.Participant;
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
public class MissionParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    public static MissionParticipant create(Mission mission, Participant participant) {
        return MissionParticipant.builder()
                .mission(mission)
                .participant(participant)
                .build();
    }
}
