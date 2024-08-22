package com.dnd.snappy.controller.v1.meeting;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.domain.meeting.dto.request.ModifyMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.ModifyMeetingResponseDto;
import com.dnd.snappy.domain.meeting.service.ModifyMeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}")
@RequiredArgsConstructor
public class ModifyMeetingController {

    private final ModifyMeetingService modifyMeetingService;

    @PatchMapping()
    public ResponseEntity<ResponseDto<ModifyMeetingResponseDto>> modifyMeeting(
            @PathVariable Long meetingId,
            @AuthPrincipal AuthInfo authInfo,
            @Valid @RequestBody ModifyMeetingRequestDto requestDto
    ) {
        var response = modifyMeetingService.modifyMeeting(meetingId, authInfo.participantId(), requestDto);
        return ResponseDto.ok(response);
    }
}
