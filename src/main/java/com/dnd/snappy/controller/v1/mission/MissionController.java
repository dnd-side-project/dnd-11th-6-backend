package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.request.ModifyMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.response.*;
import com.dnd.snappy.domain.mission.service.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping
    public ResponseEntity<ResponseDto<CreateMissionResponseDto>> createMission(
            @PathVariable Long meetingId,
            @AuthPrincipal AuthInfo authInfo,
            @Valid @RequestBody CreateMissionRequestDto requestDto
    ) {
        var response = missionService.createMission(meetingId, authInfo.participantId(), requestDto);
        return ResponseDto.created(response);
    }

    @PatchMapping("/{missionId}")
    public ResponseEntity<ResponseDto<ModifyMissionResponseDto>> modifyMission(
            @PathVariable Long meetingId,
            @PathVariable Long missionId,
            @AuthPrincipal AuthInfo authInfo,
            @Valid @RequestBody ModifyMissionRequestDto requestDto
    ) {
        var response = missionService.modifyMission(meetingId, missionId, authInfo.participantId(), requestDto);
        return ResponseDto.ok(response);
    }

    @DeleteMapping("/{missionId}")
    public ResponseEntity<ResponseDto<String>> deleteMission(
            @PathVariable Long meetingId,
            @PathVariable Long missionId,
            @AuthPrincipal AuthInfo authInfo
    ) {
        missionService.deleteMission(meetingId, missionId, authInfo.participantId());
        return ResponseDto.successMessage("모임 미션 삭제 성공했습니다.");
    }
}
