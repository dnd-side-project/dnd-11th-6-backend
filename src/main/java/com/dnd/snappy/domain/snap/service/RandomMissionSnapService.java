package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.common.dto.request.CursorBasedRequestDto;
import com.dnd.snappy.domain.common.dto.response.CursorBasedResponseDto;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.dto.response.SnapResponseDto;
import com.dnd.snappy.domain.snap.entity.RandomMissionSnap;
import com.dnd.snappy.domain.snap.entity.SimpleSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.repository.RandomMissionSnapRepository;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RandomMissionSnapService {

    private final SnapSetupManager snapSetupManager;

    private final SnapRepository snapRepository;

    private final RandomMissionRepository randomMissionRepository;

    private final RandomMissionSnapRepository randomMissionSnapRepository;

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

    public CursorBasedResponseDto<List<SnapResponseDto>> findSnapsInMeeting(CursorBasedRequestDto cursorBasedRequestDto, Long meetingId) {
        Long count = randomMissionSnapRepository.countByMeetingId(meetingId);
        if (count == 0L) {
            return CursorBasedResponseDto.empty(List.of());
        }

        Long cursorId = getCursorId(cursorBasedRequestDto.cursorId(), meetingId);
        PageRequest pageable = PageRequest.of(0, cursorBasedRequestDto.limit());

        List<SnapResponseDto> snapResponse = randomMissionSnapRepository.findRandomMissionSnapsInMeetingByCursorId(cursorId, meetingId, pageable);
        if(snapResponse.isEmpty()){
            return CursorBasedResponseDto.empty(List.of());
        }

        SnapResponseDto lastSnapResponse = snapResponse.get(snapResponse.size() - 1);
        return new CursorBasedResponseDto<>(
                lastSnapResponse.snapId(),
                snapResponse,
                count,
                cursorBasedRequestDto.limit() == snapResponse.size()
        );
    }

    private Long getCursorId(Optional<Long> cursorId, Long meetingId) {
        return cursorId.orElseGet(() ->
                randomMissionSnapRepository.findFirstByMeetingIdOrderByIdDesc(meetingId)
                        .map(snap -> snap.getId() + 1)
                        .orElse(0L));
    }

    private RandomMission findRandomMissionOrThrow(Integer randomMissionId) {
        return randomMissionRepository.findById(randomMissionId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.NOT_FOUND_RANDOM_MISSION, randomMissionId));
    }
}
