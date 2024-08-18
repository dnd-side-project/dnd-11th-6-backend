package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.MeetingMissionSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.exception.SnapErrorCode;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MeetingMissionSnapService {

    private final SnapSetupManager snapSetupManager;

    private final SnapRepository snapRepository;

    private final MissionRepository missionRepository;

    private final MissionParticipantRepository missionParticipantRepository;

    @Transactional
    public CreateSnapResponseDto create(Long meetingId, Long participantId, Long missionId, MultipartFile file, LocalDateTime shootDate) {
        SnapSetupDto snapSetupDto = snapSetupManager.setup(meetingId, participantId, file);
        Mission mission = findMissionOrThrow(meetingId, missionId);
        if(missionParticipantRepository.existsByMissionIdAndParticipantId(missionId, participantId)) {
            throw new BusinessException(MissionErrorCode.ALREADY_COMPLETED_MEETING_MISSION);
        }
        Snap snap = MeetingMissionSnap.create(snapSetupDto.snapUrl(), shootDate, snapSetupDto.meeting(), snapSetupDto.participant(), mission);
        snapRepository.save(snap);
        missionParticipantRepository.save(MissionParticipant.create(mission, snapSetupDto.participant()));
        return new CreateSnapResponseDto(
                snap.getId(),
                snap.getSnapUrl()
        );
    }

    private Mission findMissionOrThrow(Long meetingId, Long missionId) {
        return missionRepository.findByIdAndMeetingId(missionId, meetingId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.NOT_FOUND_MEETING_MISSION, missionId));
    }
}
