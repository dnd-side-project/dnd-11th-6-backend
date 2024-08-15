package com.dnd.snappy.controller.v1.meeting;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.meeting.request.LeaderAuthKeyValidationRequest;
import com.dnd.snappy.controller.v1.meeting.request.LeaderValidationRequest;
import com.dnd.snappy.controller.v1.meeting.request.PasswordValidationRequest;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.ShareMeetingLinkResponseDto;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<ResponseDto<MeetingDetailResponseDto>> findByMeetingLink(
            @RequestParam("meetingLink") String meetingLink) {
        var response = meetingService.findByMeetingLink(meetingLink);
        return ResponseDto.ok(response);
    }

    @PostMapping("/{meetingId}/validate-password")
    public ResponseEntity<ResponseDto<?>> validateMeetingPassword(
            @PathVariable("meetingId") Long meetingId,
            @Valid @RequestBody PasswordValidationRequest passwordValidationRequest
    ) {
        meetingService.validateMeetingPassword(meetingId, passwordValidationRequest.password());
        return ResponseDto.ok();
    }

    @PostMapping("/{meetingId}/validate-leader-key")
    public ResponseEntity<ResponseDto<?>> validateMeetingAuthKey(
            @PathVariable("meetingId") Long meetingId,
            @Valid @RequestBody LeaderAuthKeyValidationRequest leaderAuthKeyValidationRequest
    ) {
        meetingService.validateMeetingLeaderAuthKey(meetingId, leaderAuthKeyValidationRequest.leaderAuthKey());
        return ResponseDto.ok();
    }

    @PostMapping
    public ResponseEntity<ResponseDto<CreateMeetingResponseDto>> createMeeting(
            @RequestPart("meeting") @Valid CreateMeetingRequestDto requestDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        var response = meetingService.createMeeting(requestDto, thumbnail);
        return ResponseDto.ok(response);
    }

    @GetMapping("/{meetingId}/share")
    public ResponseEntity<ResponseDto<ShareMeetingLinkResponseDto>> getShareableMeetingLink(
            @PathVariable Long meetingId) {
        var response = meetingService.getShareableMeetingLink(meetingId);
        return ResponseDto.ok(response);
    }

}
