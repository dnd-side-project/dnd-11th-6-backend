package com.dnd.snappy.controller.v1.participant.resolver;

import com.dnd.snappy.controller.v1.auth.JwtTokenExtractor;
import com.dnd.snappy.controller.v1.auth.PathVariableExtractor;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class ParticipantArgumentResolver implements HandlerMethodArgumentResolver {

    private final PathVariableExtractor pathVariableExtractor;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Long meetingId = pathVariableExtractor.extractMeetingId(request);
        final String token = jwtTokenExtractor.extractToken(request, meetingId, TokenType.ACCESS_TOKEN);
        Long participantId = tokenProvider.extractPayload(token);
        return new AuthInfo(participantId);
    }
}
