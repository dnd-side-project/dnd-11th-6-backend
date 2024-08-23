package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.ModifyMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.ModifyMeetingResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModifyMeetingService {
    private final ParticipantRepository participantRepository;
    private final MeetingRepository meetingRepository;

    @Transactional
    public ModifyMeetingResponseDto modifyMeeting(Long meetingId, Long participantId, ModifyMeetingRequestDto requestDto) {
        Meeting meeting = findByMeetingIdOrThrow(meetingId);
        Participant participant = findParticipantByIdOrThrow(participantId);

        validateLeader(participant, meetingId);

        if (!meeting.modifyMeeting(requestDto)) {
            throw new BusinessException(MeetingErrorCode.NO_MODIFICATION);
        }

        meeting.modifyMeeting(requestDto);

        return new ModifyMeetingResponseDto(meeting.getId(), meeting.getName(), meeting.getDescription(), meeting.getSymbolColor());
    }

    private Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_NOT_FOUND, meetingId));
    }

    private Participant findParticipantByIdOrThrow(Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ParticipantErrorCode.NOT_PARTICIPATING_MEETING, participantId));
    }

    private void validateLeader(Participant participant, Long meetingId) {
        if (!participant.isLeader(meetingId)) {
            throw new BusinessException(MeetingErrorCode.UNAUTHORIZED_MEETING);
        }
    }

}
