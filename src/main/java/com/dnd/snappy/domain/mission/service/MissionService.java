package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionEntityDto;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.request.ModifyMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.response.*;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MeetingRepository meetingRepository;
    private final MissionValidationService missionValidationService;

    @Transactional
    public CreateMissionResponseDto createMission(Long meetingId, Long participantId, CreateMissionRequestDto requestDto) {
        missionValidationService.validateIsLeader(participantId, meetingId);

        CreateMissionEntityDto missionDto = CreateMissionEntityDto.from(requestDto, meetingId);
        Mission mission = Mission.create(missionDto, findByMeetingIdOrThrow(meetingId));
        missionRepository.save(mission);

        return new CreateMissionResponseDto(mission.getId(), mission.getContent());
    }

    @Transactional
    public ModifyMissionResponseDto modifyMission(Long meetingId, Long missionId, Long participantId, ModifyMissionRequestDto requestDto) {
        missionValidationService.validateIsLeader(participantId, meetingId);
        Mission mission = findMissionByIdOrThrow(missionId);

        missionValidationService.validateModification(mission, requestDto.content(), participantId, meetingId);

        mission.setContent(requestDto.content());

        return new ModifyMissionResponseDto(mission.getId(), mission.getContent());
    }

    @Transactional
    public void deleteMission(Long meetingId, Long missionId, Long participantId) {
        missionValidationService.validateMissionForDeletion(missionId, participantId, meetingId);

        Mission mission = findMissionByIdOrThrow(missionId);
        missionRepository.delete(mission);
    }

    private Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_NOT_FOUND, meetingId));
    }

    private Mission findMissionByIdOrThrow(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.MISSION_NOT_FOUND, missionId));
    }
}
