package com.dnd.snappy.domain.member.repository;

import com.dnd.snappy.domain.member.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    boolean existsByMemberIdAndMeetingId(Long memberId, Long meetingId);

    boolean existsByNicknameAndMeetingId(String nickname, Long meetingId);
}
