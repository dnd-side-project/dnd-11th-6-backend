package com.dnd.snappy.controller.v1.participant;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.controller.v1.participant.response.CheckDuplicateNicknameResponse;
import com.dnd.snappy.controller.v1.snap.request.CursorBasedRequest;
import com.dnd.snappy.domain.auth.service.AuthCookieManager;
import com.dnd.snappy.controller.v1.participant.request.ParticipationRequest;
import com.dnd.snappy.controller.v1.participant.response.ParticipationResponse;
import com.dnd.snappy.domain.common.dto.response.CursorBasedResponseDto;
import com.dnd.snappy.domain.participant.dto.response.ParticipantDetailResponseDto;
import com.dnd.snappy.domain.participant.dto.response.ParticipantResponseDto;
import com.dnd.snappy.domain.participant.service.ParticipantService;
import com.dnd.snappy.domain.participant.service.ParticipationService;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipationService participationService;

    private final ParticipantService participantService;

    private final AuthCookieManager authCookieManager;

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
        String accessTokenCookie = authCookieManager.createTokenCookie(TokenType.ACCESS_TOKEN, response.accessToken(), meetingId, duration);
        String refreshTokenCookie = authCookieManager.createTokenCookie(TokenType.REFRESH_TOKEN, response.refreshToken(), meetingId, duration);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new ResponseDto<>(
                        HttpStatus.OK.value(),
                        new ParticipationResponse(response.participantId()),
                        null
                ));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseDto<ParticipantDetailResponseDto>> getParticipantInMeeting(
            @AuthPrincipal AuthInfo authInfo
    ) {
        var response = participantService.findParticipantDetailById(authInfo.participantId());

        return ResponseDto.ok(response);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ResponseDto<CheckDuplicateNicknameResponse>> checkDuplicateNickname(
            @PathVariable("meetingId") Long meetingId,
            @RequestParam("nickname") @NotBlank(message = "nickname은 필수입니다.") String nickname
    ) {
        boolean isDuplicatedNickname = participantService.checkDuplicateNickname(meetingId, nickname);

        return ResponseDto.ok(new CheckDuplicateNicknameResponse(!isDuplicatedNickname));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<CursorBasedResponseDto<List<ParticipantResponseDto>>>> findParticipantsInMeeting(
            @PathVariable("meetingId") Long meetingId,
            @ModelAttribute CursorBasedRequest cursorBasedRequest
    ) {
        var data = participantService.findParticipantsInMeeting(cursorBasedRequest.toCursorBasedRequestDto(), meetingId);
        return ResponseDto.ok(data);
    }
}
