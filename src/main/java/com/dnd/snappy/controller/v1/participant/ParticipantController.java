package com.dnd.snappy.controller.v1.participant;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.domain.auth.service.AuthCookieManager;
import com.dnd.snappy.controller.v1.participant.request.ParticipationRequest;
import com.dnd.snappy.controller.v1.participant.response.ParticipationResponse;
import com.dnd.snappy.domain.participant.service.ParticipationService;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private final AuthCookieManager authTokenManager;

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

        Duration duration = Duration.between(LocalDateTime.now(), response.meetingExpiredDate());
        String accessTokenCookie = authTokenManager.createTokenCookie(TokenType.ACCESS_TOKEN, response.accessToken(), meetingId, duration);
        String refreshTokenCookie = authTokenManager.createTokenCookie(TokenType.REFRESH_TOKEN, response.refreshToken(), meetingId, duration);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new ResponseDto<>(
                        HttpStatus.OK.value(),
                        new ParticipationResponse(response.participantId(), response.accessToken()),
                        null
                ));
    }
}
