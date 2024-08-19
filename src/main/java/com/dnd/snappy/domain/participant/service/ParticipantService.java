package com.dnd.snappy.domain.participant.service;

import static com.dnd.snappy.domain.meeting.exception.MeetingErrorCode.*;
import static com.dnd.snappy.domain.participant.exception.ParticipantErrorCode.DUPLICATED_NICKNAME;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.dto.response.CreateParticipantResponseDto;
import com.dnd.snappy.domain.participant.dto.response.ParticipantDetailResponseDto;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public CreateParticipantResponseDto createParticipant(Long meetingId, String nickname, Role role) {
        validationCreateParticipant(meetingId, nickname);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND));
        if(!meeting.canJoinMeeting()) {
            throw new BusinessException(MEETING_JOIN_DENIED);
        }

        Participant participant = Participant.create(nickname, role, meeting);
        participantRepository.save(participant);

        return new CreateParticipantResponseDto(participant.getId(), meeting.getExpiredDate());
    }

    public boolean checkDuplicateNickname(Long meetingId, String nickname) {
        return participantRepository.existsByNicknameAndMeetingId(nickname, meetingId);
    }

    public ParticipantDetailResponseDto findParticipantDetailById(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ParticipantErrorCode.NOT_FOUND_PARTICIPANT_ID));
        return new ParticipantDetailResponseDto(
                participant.getId(),
                participant.getNickname(),
                participant.getRole(),
                participant.getShootCount()
        );
    }

    private void validationCreateParticipant(Long meetingId, String nickname) {
        if(participantRepository.existsByNicknameAndMeetingId(nickname, meetingId)) {
            throw new BusinessException(DUPLICATED_NICKNAME);
        }
    }

}
