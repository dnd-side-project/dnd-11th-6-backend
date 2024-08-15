package com.dnd.snappy.controller.v1.auth;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.RefreshAuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.RefreshAuthPrincipal;
import com.dnd.snappy.domain.auth.service.AuthCookieManager;
import com.dnd.snappy.domain.auth.service.AuthService;
import com.dnd.snappy.domain.token.service.TokenType;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieManager authCookieManager;

    @PostMapping("/meetings/{meetingId}/tokens/refresh")
    public ResponseEntity<?> reissueToken(
            @PathVariable Long meetingId,
            @RefreshAuthPrincipal RefreshAuthInfo authInfo
    ) {
        var response = authService.reissueTokens(meetingId, authInfo.participantId(), authInfo.refreshToken());

        Duration duration = Duration.between(LocalDateTime.now(), response.meetingExpiredDate());
        String accessTokenCookie = authCookieManager.createTokenCookie(TokenType.ACCESS_TOKEN, response.accessToken(), meetingId, duration);
        String refreshTokenCookie = authCookieManager.createTokenCookie(TokenType.REFRESH_TOKEN, response.refreshToken(), meetingId, duration);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new ResponseDto<>(
                        HttpStatus.OK.value(),
                        null,
                        null
                ));
    }
}
