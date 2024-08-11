package com.dnd.snappy.controller.v1.participant;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.CookieManager;
import com.dnd.snappy.controller.v1.participant.request.ParticipationRequest;
import com.dnd.snappy.controller.v1.participant.response.ParticipationResponse;
import com.dnd.snappy.domain.participant.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipationService participationService;

    private final CookieManager cookieManager;

    @PostMapping
    public ResponseEntity<ResponseDto<ParticipationResponse>> participateMeeting(
            @PathVariable Long meetingId,
            @Valid @RequestBody ParticipationRequest participationRequestDto
    ) {

        var response = participationService.participate(
                meetingId,
                participationRequestDto.nickname(),
                participationRequestDto.role()
        );

        //TODO: access token도 쿠키로
        String cookie = cookieManager.createRefreshTokenCookie(response.refreshToken(), meetingId, "/api/", 3600L);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(new ResponseDto<>(
                        true,
                        new ParticipationResponse(response.participantId(), response.accessToken()),
                        null
                ));
    }
}