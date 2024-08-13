package com.dnd.snappy.controller.v1.auth.interceptor;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.service.JwtTokenExtractor;
import com.dnd.snappy.domain.auth.service.PathVariableExtractor;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MeetingParticipationInterceptor implements HandlerInterceptor {

    public static final String PARTICIPATION_URL_PATTERN = "/api/.*/meetings/\\d+/participants";

    private final PathVariableExtractor pathVariableExtractor;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final TokenProvider tokenProvider;
    private final ParticipantRepository participantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isMeetingParticipationRequest(request)) {
            return true;
        }

        final Long meetingId = pathVariableExtractor.extractMeetingId(request);
        final String token = jwtTokenExtractor.extractToken(request, meetingId, TokenType.ACCESS_TOKEN);
        final Long participantId = tokenProvider.extractPayload(token);
        validationParticipantInMeeting(participantId, meetingId);

        return true;
    }

    private void validationParticipantInMeeting(Long participantId, Long meetingId) {
        if(!participantRepository.existsByIdAndMeetingId(participantId, meetingId)) {
            throw new BusinessException(ParticipantErrorCode.NOT_PARTICIPATING_MEETING);
        }
    }

    private boolean isMeetingParticipationRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.matches(PARTICIPATION_URL_PATTERN) && request.getMethod().equalsIgnoreCase("POST");
    }
}
