package com.dnd.snappy.controller.v1.auth.interceptor;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.dto.response.TokenInfo;
import com.dnd.snappy.domain.auth.exception.AuthErrorCode;
import com.dnd.snappy.domain.auth.service.JwtTokenStrategy;
import com.dnd.snappy.domain.auth.service.PathVariableExtractor;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final PathVariableExtractor pathVariableExtractor;
    private final JwtTokenStrategy jwtTokenStrategy;
    private final ParticipantRepository participantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        final Long meetingId = pathVariableExtractor.extractMeetingId(request);
        TokenInfo tokenInfo = jwtTokenStrategy.loginProcess(request);
        validationParticipantInMeeting(tokenInfo.payload(), meetingId);
        return true;
    }

    private void validationParticipantInMeeting(Long participantId, Long meetingId) {
        if(!participantRepository.existsByIdAndMeetingId(participantId, meetingId)) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
    }
}
