package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.domain.mission.dto.response.MissionDetailResponseDto;
import com.dnd.snappy.domain.mission.service.MissionParticipantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}/missions")
@RequiredArgsConstructor
public class MissionParticipantController {

    private final MissionParticipantService missionParticipantService;

    @GetMapping("/completed")
    public ResponseEntity<ResponseDto<List<MissionDetailResponseDto>>> findCompletedMissionsByParticipantId(
            @PathVariable Long meetingId,
            @AuthPrincipal AuthInfo authInfo
    ) {
        var response = missionParticipantService.findCompletedMissionsByParticipantId(authInfo.participantId());
        return ResponseDto.ok(response);
    }

    @GetMapping("/incomplete")
    public ResponseEntity<ResponseDto<List<MissionDetailResponseDto>>> findNotCompletedMissionsByParticipant(
            @PathVariable Long meetingId,
            @AuthPrincipal AuthInfo authInfo
    ) {
        var response = missionParticipantService.findNotCompletedMissionsByParticipant(meetingId, authInfo.participantId());
        return ResponseDto.ok(response);
    }
}
