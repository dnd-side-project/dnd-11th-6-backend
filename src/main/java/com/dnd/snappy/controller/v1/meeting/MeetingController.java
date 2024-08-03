package com.dnd.snappy.controller.v1.meeting;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<ResponseDto<MeetingDetailResponseDto>> findByMeetingLink(@RequestParam("meetingLink") String meetingLink) {
        var response = meetingService.findByMeetingLink(meetingLink);
        return ResponseDto.ok(response);
    }
}
