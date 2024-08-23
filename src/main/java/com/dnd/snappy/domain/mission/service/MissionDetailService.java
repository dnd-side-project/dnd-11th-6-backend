package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.dto.response.LeaderMeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.dto.response.MeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionDetailService {

    private final MissionRepository missionRepository;
    private final MeetingRepository meetingRepository;
    private final MissionValidationService missionValidationService;

    @Transactional(readOnly = true)
    public List<MeetingMissionDetailResponseDto> findMeetingMissions(Long meetingId, Long participantId) {
        findByMeetingIdOrThrow(meetingId);
        List<Mission> missions = missionRepository.findAllByMeetingId(meetingId);

        return missions.stream()
                .map(mission -> new MeetingMissionDetailResponseDto(mission.getId(), mission.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaderMeetingMissionDetailResponseDto> findLeaderMeetingMissions(Long meetingId, Long participantId) {
        findByMeetingIdOrThrow(meetingId);
        missionValidationService.validateIsLeader(participantId, meetingId);

        return missionRepository.findLeaderMeetingMissions(meetingId, participantId);
    }

    private Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_NOT_FOUND, meetingId));
    }
}
