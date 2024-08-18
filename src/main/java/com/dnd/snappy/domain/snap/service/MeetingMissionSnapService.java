package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.MeetingMissionSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
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

    @Transactional
    public CreateSnapResponseDto create(Long meetingId, Long participantId, Long missionId, MultipartFile file, LocalDateTime shootDate) {
        SnapSetupDto snapSetupDto = snapSetupManager.setup(meetingId, participantId, file);
        Mission mission = missionRepository.findByIdAndMeetingId(missionId, meetingId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.NOT_FOUND_MEETING_MISSION, missionId));

        Snap snap = MeetingMissionSnap.create(snapSetupDto.snapUrl(), shootDate, snapSetupDto.meeting(), snapSetupDto.participant(), mission);
        snapRepository.save(snap);

        return new CreateSnapResponseDto(
                snap.getId(),
                snap.getSnapUrl()
        );
    }
}
