package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionEntityDto;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.response.CreateMissionResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;
    private final MeetingRepository meetingRepository;

    @Transactional
    public CreateMissionResponseDto createMission(Long meetingId, Long participantId, CreateMissionRequestDto requestDto) {
        Participant participant = getValidatedParticipant(participantId);

        CreateMissionEntityDto missionDto = CreateMissionEntityDto.from(requestDto, meetingId);

        Mission mission = Mission.create(missionDto, findByMeetingIdOrThrow(meetingId));
        missionRepository.save(mission);

        return new CreateMissionResponseDto(mission.getContent());
    }

    private Participant getValidatedParticipant(Long participantId) {
        Participant participant = findParticipantById(participantId);

        if (!participant.isLeader()) {
            throw new BusinessException(MissionErrorCode.UNAUTHORIZED_MISSION_CREATION);
        }

        return participant;
    }

    private Participant findParticipantById(Long participantId) {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ParticipantErrorCode.NOT_PARTICIPATING_MEETING));
    }

    private Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_NOT_FOUND, meetingId));
    }
}
