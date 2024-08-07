package com.dnd.snappy.controller.v1.member;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.member.request.ParticipationRequestDto;
import com.dnd.snappy.domain.member.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.member.service.MemberService;
import com.dnd.snappy.domain.member.service.ParticipationService;
import com.dnd.snappy.domain.token.service.JwtTokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final JwtTokenExtractor jwtTokenExtractor;

    @PostMapping("/meetings/{meetingId}/members")
    public ResponseEntity<ResponseDto<ParticipationResponseDto>> participateMeeting(
            @PathVariable Long meetingId,
            @Valid @RequestBody ParticipationRequestDto participationRequestDto,
            HttpServletRequest request
    ) {

        Optional<String> accessToken = jwtTokenExtractor.extractOptionalAccessToken(request);

        var response = participationService.participate(
                accessToken,
                meetingId,
                participationRequestDto.nickname(),
                participationRequestDto.role()
        );

        return ResponseDto.ok(response);
    }
}
