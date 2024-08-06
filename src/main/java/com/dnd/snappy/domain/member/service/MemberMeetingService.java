package com.dnd.snappy.domain.member.service;

import static com.dnd.snappy.domain.meeting.exception.MeetingErrorCode.*;
import static com.dnd.snappy.domain.member.exception.MemberErrorCode.*;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.member.entity.Member;
import com.dnd.snappy.domain.member.entity.MemberMeeting;
import com.dnd.snappy.domain.member.entity.Role;
import com.dnd.snappy.domain.member.repository.MemberMeetingRepository;
import com.dnd.snappy.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberMeetingService {

    private final MemberMeetingRepository memberMeetingRepository;

    private final MeetingRepository meetingRepository;

    @Transactional
    public void joinMeeting(Long memberId, Long meetingId, String nickname, Role role) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND));

        MemberMeeting memberMeeting = MemberMeeting.create(nickname, Member.Id(memberId), meeting, role);
        memberMeetingRepository.save(memberMeeting);
    }

}
