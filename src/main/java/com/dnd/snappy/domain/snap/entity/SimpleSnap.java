package com.dnd.snappy.domain.snap.entity;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.participant.entity.Participant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("SIMPLE")
public class SimpleSnap extends Snap {

    private SimpleSnap(String snapUrl, LocalDateTime shootDate, Meeting meeting, Participant participant) {
        super(snapUrl, shootDate, meeting, participant);
    }

    public static SimpleSnap create(String snapUrl, LocalDateTime shootDate, Meeting meeting, Participant participant) {
        meeting.validateCanShoot();
        participant.addShootCount();
        return new SimpleSnap(snapUrl, shootDate, meeting, participant);
    }
}
