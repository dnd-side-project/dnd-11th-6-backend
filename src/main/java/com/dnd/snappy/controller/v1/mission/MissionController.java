package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.response.CreateMissionResponseDto;
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
        return ResponseDto.ok(response);
    }

}
