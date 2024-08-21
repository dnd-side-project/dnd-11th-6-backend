package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.domain.mission.dto.response.LeaderMeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.dto.response.MeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.service.MissionDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}/missions")
@RequiredArgsConstructor
public class MissionDetailController {

    private final MissionDetailService missionDetailService;

    @GetMapping()
    public ResponseEntity<ResponseDto<List<MeetingMissionDetailResponseDto>>> findMeetingMissions(
            @PathVariable Long meetingId,
            @AuthPrincipal AuthInfo authInfo
    ) {
        var data = missionDetailService.findMeetingMissions(meetingId, authInfo.participantId());
        return ResponseDto.ok(data);
    }

    @GetMapping("/leader")
    public ResponseEntity<ResponseDto<List<LeaderMeetingMissionDetailResponseDto>>> findLeaderMeetingMissions(
            @PathVariable Long meetingId,
            @AuthPrincipal AuthInfo authInfo
    ) {
        var data = missionDetailService.findLeaderMeetingMissions(meetingId, authInfo.participantId());
        return ResponseDto.ok(data);
    }
}
