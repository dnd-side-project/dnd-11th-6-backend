package com.dnd.snappy.controller.v1.member;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.CookieManager;
import com.dnd.snappy.controller.v1.member.request.ParticipationRequestDto;
import com.dnd.snappy.controller.v1.member.response.ParticipationResponse;
import com.dnd.snappy.domain.member.service.ParticipationService;
import com.dnd.snappy.controller.JwtTokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    private final JwtTokenExtractor jwtTokenExtractor;

    private final CookieManager cookieManager;

    @PostMapping("/members")
    public ResponseEntity<ResponseDto<ParticipationResponse>> participateMeeting(
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

        String cookie = cookieManager.createNewCookie(response.refreshToken(), "/api/v1/");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(new ResponseDto<>(
                        true,
                        new ParticipationResponse(response.memberId(), response.accessToken()),
                        null
                ));
    }
}
