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

@Service
@RequiredArgsConstructor
public class MemberMeetingService {

    private final MemberRepository memberRepository;

    private final MemberMeetingRepository memberMeetingRepository;

    private final MeetingRepository meetingRepository;

    public Long joinMeeting(Long memberId, Long meetingId, String nickname, Role role) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND));

        // memberId가 없을때 -> 지금 진행하고 있는 모임이 없을경우(access token or refresh token이 없을때?)
        Member member = Optional.ofNullable(memberId)
                .map(id -> memberRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(MEMBER_NOT_FOUND, id)))
                .orElseGet(() -> memberRepository.save(Member.create()));


        MemberMeeting memberMeeting = MemberMeeting.create(nickname, member, meeting, role);
        memberMeetingRepository.save(memberMeeting);

        return member.getId();
    }

}
