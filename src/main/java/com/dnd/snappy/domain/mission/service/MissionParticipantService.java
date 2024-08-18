package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.domain.mission.dto.response.MissionDetailResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MissionParticipantService {

    private final MissionParticipantRepository missionParticipantRepository;

    public List<MissionDetailResponseDto> findCompletedMissions(Long participantId) {
        List<Mission> missions = missionParticipantRepository.findCompletedMissions(participantId);
        return missions.stream()
                .map(mission -> new MissionDetailResponseDto(mission.getId(), mission.getContent()))
                .toList();
    }

    public List<MissionDetailResponseDto> findNotCompletedMissions(Long meetingId, Long participantId) {
        List<Mission> missions = missionParticipantRepository.findNotCompletedMissions(meetingId, participantId);
        return missions.stream()
                .map(mission -> new MissionDetailResponseDto(mission.getId(), mission.getContent()))
                .toList();
    }


}
