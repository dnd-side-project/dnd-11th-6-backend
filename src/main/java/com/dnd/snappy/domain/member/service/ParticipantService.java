package com.dnd.snappy.domain.member.service;

import static com.dnd.snappy.domain.meeting.exception.MeetingErrorCode.*;
import static com.dnd.snappy.domain.member.exception.ParticipantErrorCode.ALREADY_PARTICIPATE_MEETING;
import static com.dnd.snappy.domain.member.exception.ParticipantErrorCode.DUPLICATED_NICKNAME;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.member.entity.Member;
import com.dnd.snappy.domain.member.entity.Participant;
import com.dnd.snappy.domain.member.entity.Role;
import com.dnd.snappy.domain.member.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public void joinMeeting(Long memberId, Long meetingId, String nickname, Role role) {
        validationJoinMeeting(memberId, meetingId, nickname);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND));
        if(!meeting.canJoinMeeting()) {
            throw new BusinessException(MEETING_JOIN_DENIED);
        }

        Participant memberMeeting = Participant.create(nickname, role, Member.Id(memberId), meeting);
        participantRepository.save(memberMeeting);
    }

    private void validationJoinMeeting(Long memberId, Long meetingId, String nickname) {
        if(participantRepository.existsByMemberIdAndMeetingId(memberId, meetingId)) {
            throw new BusinessException(ALREADY_PARTICIPATE_MEETING);
        }
        if(participantRepository.existsByNicknameAndMeetingId(nickname, meetingId)) {
            throw new BusinessException(DUPLICATED_NICKNAME);
        }
    }

}
