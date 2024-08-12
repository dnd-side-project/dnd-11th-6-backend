package com.dnd.snappy.controller.v1.auth.interceptor;

import static com.dnd.snappy.common.error.CommonErrorCode.*;
import static com.dnd.snappy.domain.token.exception.TokenErrorCode.*;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.controller.v1.auth.JwtTokenExtractor;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class MeetingParticipationInterceptor implements HandlerInterceptor {

    private static final String PATH_VARIABLE_KEY = "meetingId";
    public static final String PARTICIPATION_URL_PATTERN = "/api/.*/meeting/\\d+/participants";

    private final JwtTokenExtractor jwtTokenExtractor;
    private final TokenProvider tokenProvider;
    private final ParticipantRepository participantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isMeetingParticipationUrl(request)) {
            return true;
        }

        Long meetingId = getMeetingId(request);
        final String token = jwtTokenExtractor.extractToken(request, meetingId, TokenType.ACCESS_TOKEN);
        Long participantId = tokenProvider.extractPayload(token);
        validationParticipantInMeeting(participantId, meetingId);

        return true;
    }

    private boolean isMeetingParticipationUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri.matches(PARTICIPATION_URL_PATTERN) && request.getMethod().equalsIgnoreCase("POST");
    }

    private Long getMeetingId(HttpServletRequest request) {
        final Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(pathVariables == null || Objects.isNull(pathVariables.get(PATH_VARIABLE_KEY))) {
            throw new BusinessException(BAD_REQUEST, "No path variables meetingId found in request");
        }
        return Long.parseLong(pathVariables.get(PATH_VARIABLE_KEY));
    }

    public void validationParticipantInMeeting(Long participantId, Long meetingId) {
        if(!participantRepository.existsByIdAndMeetingId(participantId, meetingId)) {
            throw new BusinessException(ParticipantErrorCode.NOT_PARTICIPATING_MEETING);
        }
    }
}
