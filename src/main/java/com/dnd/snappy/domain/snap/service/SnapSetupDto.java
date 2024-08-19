package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.participant.entity.Participant;

public record SnapSetupDto(
        Meeting meeting,
        Participant participant,
        String snapUrl
) {
}
