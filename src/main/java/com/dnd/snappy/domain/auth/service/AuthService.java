package com.dnd.snappy.domain.auth.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.dto.response.ReissueTokenResponseDto;
import com.dnd.snappy.domain.auth.exception.AuthErrorCode;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final MeetingService meetingService;

    public ReissueTokenResponseDto reissueTokens(Long meetingId, Long participantId, String token) {
        if(!tokenService.equalsToken(participantId, token)) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        Meeting meeting = meetingService.findByMeetingIdOrThrow(meetingId);
        Tokens tokens = tokenService.createTokens(participantId);
        return new ReissueTokenResponseDto(
                tokens.accessToken(),
                tokens.refreshToken(),
                meeting.getExpiredDate()
        );
    }
}
