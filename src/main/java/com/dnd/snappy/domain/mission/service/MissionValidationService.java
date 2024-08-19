package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionValidationService {

    private final MissionParticipantRepository missionParticipantRepository;
    private final ParticipantRepository participantRepository;
    private final MissionRepository missionRepository;

    public void validateIsLeader(Long participantId, Long meetingId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ParticipantErrorCode.NOT_PARTICIPATING_MEETING, participantId));

        if (!participant.isLeader(meetingId)) {
            throw new BusinessException(MissionErrorCode.UNAUTHORIZED_MISSION);
        }
    }

    public void validateModification(Mission mission, String newContent, Long participantId, Long meetingId) {
        boolean hasParticipants = missionParticipantRepository.existsByMissionId(mission.getId());
        if (hasParticipants) {
            throw new BusinessException(MissionErrorCode.MISSION_HAS_PARTICIPANTS);
        }

        if (!mission.belongsToMeeting(meetingId)) {
            throw new BusinessException(MissionErrorCode.MISSION_NOT_FOUND_FOR_MEETING);
        }

        if (mission.getContent().equals(newContent)) {
            throw new BusinessException(MissionErrorCode.MISSION_CONTENT_UNCHANGED);
        }

        validateIsLeader(participantId, meetingId);
    }

    public void validateMissionForDeletion(Long missionId, Long participantId, Long meetingId) {
        boolean hasParticipants = missionParticipantRepository.existsByMissionId(missionId);
        if (hasParticipants) {
            throw new BusinessException(MissionErrorCode.MISSION_HAS_PARTICIPANTS);
        }

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.MISSION_NOT_FOUND, missionId));

        if (!mission.belongsToMeeting(meetingId)) {
            throw new BusinessException(MissionErrorCode.MISSION_NOT_FOUND_FOR_MEETING);
        }

        validateIsLeader(participantId, meetingId);
    }
}

