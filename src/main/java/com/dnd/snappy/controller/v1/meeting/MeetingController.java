package com.dnd.snappy.controller.v1.meeting;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<ResponseDto<CreateMeetingResponseDto>> createMeeting(
            @RequestPart("meeting") @Valid CreateMeetingRequestDto requestDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

        var response = meetingService.createMeeting(requestDto, thumbnail);
        return ResponseDto.ok(response);
    }
}
