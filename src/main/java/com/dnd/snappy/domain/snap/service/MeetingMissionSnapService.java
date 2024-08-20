package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.common.dto.request.CursorBasedRequestDto;
import com.dnd.snappy.domain.common.dto.response.CursorBasedResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.dto.response.SnapResponseDto;
import com.dnd.snappy.domain.snap.entity.MeetingMissionSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.exception.SnapErrorCode;
import com.dnd.snappy.domain.snap.repository.MeetingMissionSnapRepository;
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
public class MeetingMissionSnapService {

    private final SnapSetupManager snapSetupManager;

    private final SnapRepository snapRepository;

    private final MissionRepository missionRepository;

    private final MissionParticipantRepository missionParticipantRepository;

    private final MeetingMissionSnapRepository meetingMissionSnapRepository;

    @Transactional
    public CreateSnapResponseDto create(Long meetingId, Long participantId, Long missionId, MultipartFile file, LocalDateTime shootDate) {
        SnapSetupDto snapSetupDto = snapSetupManager.setup(meetingId, participantId, file);

        Mission mission = findMissionOrThrow(meetingId, missionId);
        validateAlreadyCompletedMission(participantId, missionId);

        Snap snap = MeetingMissionSnap.create(snapSetupDto.snapUrl(), shootDate, snapSetupDto.meeting(), snapSetupDto.participant(), mission);
        snapRepository.save(snap);
        missionParticipantRepository.save(MissionParticipant.create(mission, snapSetupDto.participant()));
        return new CreateSnapResponseDto(
                snap.getId(),
                snap.getSnapUrl()
        );
    }

    public CursorBasedResponseDto<List<SnapResponseDto>> findSnapsInMeeting(CursorBasedRequestDto cursorBasedRequestDto, Long meetingId) {
        Long count = meetingMissionSnapRepository.countByMeetingId(meetingId);
        if (count == 0L) {
            return CursorBasedResponseDto.empty(List.of());
        }

        Long cursorId = getCursorId(cursorBasedRequestDto.cursorId(), meetingId);
        PageRequest pageable = PageRequest.of(0, cursorBasedRequestDto.limit());

        List<SnapResponseDto> snapResponse = meetingMissionSnapRepository.findMeetingMissionSnapsInMeetingByCursorId(cursorId, meetingId, pageable);
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
                meetingMissionSnapRepository.findFirstByMeetingIdOrderByIdDesc(meetingId)
                        .map(snap -> snap.getId() + 1)
                        .orElse(0L));
    }

    private Mission findMissionOrThrow(Long meetingId, Long missionId) {
        return missionRepository.findByIdAndMeetingId(missionId, meetingId)
                .orElseThrow(() -> new NotFoundException(MissionErrorCode.NOT_FOUND_MEETING_MISSION, missionId));
    }

    private void validateAlreadyCompletedMission(Long participantId, Long missionId) {
        if(missionParticipantRepository.existsByMissionIdAndParticipantId(missionId, participantId)) {
            throw new BusinessException(MissionErrorCode.ALREADY_COMPLETED_MEETING_MISSION);
        }
    }
}
