package com.dnd.snappy.domain.auth.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.dto.response.ReissueTokenResponseDto;
import com.dnd.snappy.domain.auth.exception.AuthErrorCode;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final MeetingService meetingService;
    private final ParticipantRepository participantRepository;

    public ReissueTokenResponseDto reissueTokens(Long meetingId, Long participantId, String token) {
        if(!tokenService.equalsToken(participantId, token)) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN, "유효하지 않은 토큰입니다.");
        }
        Meeting meeting = meetingService.findByMeetingIdOrThrow(meetingId);
        if(!participantRepository.existsByIdAndMeetingId(participantId, meetingId)) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN, "참여중인 모임이 아닙니다.");
        }
        Tokens tokens = tokenService.createTokens(participantId);
        return new ReissueTokenResponseDto(
                tokens.accessToken(),
                tokens.refreshToken(),
                meeting.getExpiredDate()
        );
    }
}
