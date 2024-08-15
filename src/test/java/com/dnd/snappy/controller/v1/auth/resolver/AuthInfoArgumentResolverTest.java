package com.dnd.snappy.controller.v1.auth.resolver;

import com.dnd.snappy.domain.auth.service.AuthTokenCookieNameGenerator;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.IntegrationTestSupport;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;

class AuthInfoArgumentResolverTest extends IntegrationTestSupport {

    @Autowired
    private AuthInfoArgumentResolver authInfoArgumentResolver;

    @Autowired
    private AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("요청 정보를 통해 AuthInfo class를 만든다.")
    @Test
    void resolveArgument() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        NativeWebRequest nativeWebRequest = new ServletWebRequest(request);
        settingRequest(request, 1L, 2L);

        //when
        Object result = authInfoArgumentResolver.resolveArgument(null, null, nativeWebRequest, null);

        //then
        Assertions.assertThat(result instanceof AuthInfo).isTrue();
        Assertions.assertThat(((AuthInfo) result).participantId()).isEqualTo(2L);

    }

    private void settingRequest(MockHttpServletRequest request, Long meetingId, Long participantId) {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", meetingId + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        request.setCookies(new Cookie(
                        authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meetingId),
                        tokenProvider.issueToken(participantId, TokenType.ACCESS_TOKEN)
                )
        );
    }

}