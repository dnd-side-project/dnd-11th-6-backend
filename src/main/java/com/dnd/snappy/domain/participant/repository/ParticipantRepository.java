package com.dnd.snappy.domain.participant.repository;

import com.dnd.snappy.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    boolean existsByNicknameAndMeetingId(String nickname, Long meetingId);
}
