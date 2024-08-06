package com.dnd.snappy.domain.member.repository;

import com.dnd.snappy.domain.member.entity.MemberMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMeetingRepository extends JpaRepository<MemberMeeting, Long> {
    boolean existsByMemberIdAndMeetingId(Long memberId, Long meetingId);

    boolean existsByNicknameAndMeetingId(String nickname, Long meetingId);
}
