package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.RandomMissionSnap;
import com.dnd.snappy.domain.snap.entity.SimpleSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RandomMissionSnapService {

    private final SnapSetupManager snapSetupManager;

    private final SnapRepository snapRepository;

    private final RandomMissionRepository randomMissionRepository;

    @Transactional
    public CreateSnapResponseDto create(Long meetingId, Long participantId, Integer randomMissionId, MultipartFile file, LocalDateTime shootDate) {
        SnapSetupDto snapSetupDto = snapSetupManager.setup(meetingId, participantId, file);
        RandomMission randomMission = findRandomMissionOrThrow(randomMissionId);

        Snap snap = RandomMissionSnap.create(snapSetupDto.snapUrl(), shootDate, snapSetupDto.meeting(), snapSetupDto.participant(), randomMission);
        snapRepository.save(snap);

        return new CreateSnapResponseDto(
                snap.getId(),
                snap.getSnapUrl()
        );
    }

    private RandomMission findRandomMissionOrThrow(Integer randomMissionId) {
        return randomMissionRepository.findById(randomMissionId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.NOT_FOUND_RANDOM_MISSION, randomMissionId));
    }
}
