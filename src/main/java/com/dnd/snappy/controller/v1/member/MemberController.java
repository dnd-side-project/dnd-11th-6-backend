package com.dnd.snappy.controller.v1.member;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.member.request.ParticipationRequestDto;
import com.dnd.snappy.domain.member.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.member.service.MemberService;
import com.dnd.snappy.domain.member.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final ParticipationService participationService;

    @PostMapping("/meetings/{meetingId}/members")
    public ResponseEntity<ResponseDto<ParticipationResponseDto>> participateMeeting(
            @PathVariable Long meetingId,
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @Valid @RequestBody ParticipationRequestDto participationRequestDto
    ) {

        var response = participationService.participate(
                refreshToken,
                meetingId,
                participationRequestDto.nickname(),
                participationRequestDto.role()
        );

        return ResponseDto.ok(response);
    }
}
