package com.dnd.snappy.controller.v1.auth.interceptor;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.controller.v1.auth.JwtTokenExtractor;
import com.dnd.snappy.controller.v1.auth.PathVariableExtractor;
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

    public static final String PARTICIPATION_URL_PATTERN = "/api/.*/meeting/\\d+/participants";

    private final PathVariableExtractor pathVariableExtractor;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final TokenProvider tokenProvider;
    private final ParticipantRepository participantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isMeetingParticipationUrl(request)) {
            return true;
        }

        Long meetingId = pathVariableExtractor.extractMeetingId(request);
        final String token = jwtTokenExtractor.extractToken(request, meetingId, TokenType.ACCESS_TOKEN);
        Long participantId = tokenProvider.extractPayload(token);
        validationParticipantInMeeting(participantId, meetingId);

        return true;
    }

    private boolean isMeetingParticipationUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.matches(PARTICIPATION_URL_PATTERN) && request.getMethod().equalsIgnoreCase("POST");
    }

    public void validationParticipantInMeeting(Long participantId, Long meetingId) {
        if(!participantRepository.existsByIdAndMeetingId(participantId, meetingId)) {
            throw new BusinessException(ParticipantErrorCode.NOT_PARTICIPATING_MEETING);
        }
    }
}
