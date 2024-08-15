package com.dnd.snappy.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.dnd.snappy.domain.auth.dto.response.TokenInfo;
import com.dnd.snappy.domain.token.service.JwtProperties;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

class JwtTokenStrategyTest {

    private static final String testSecretKey = "test-secret-test-secret-test-secret-test-secret";
    private static final AuthTokenCookieNameGenerator authTokenCookieNameGenerator = new AuthTokenCookieNameGenerator();
    private static final JwtProperties jwtProperties = new JwtProperties(testSecretKey, 18000L, 36000L);

    private JwtTokenExtractor jwtTokenExtractor;
    private TokenProvider tokenProvider;
    private JwtTokenStrategy sut;

    @BeforeEach
    void setUp() {
        jwtTokenExtractor = new JwtTokenExtractor(new PathVariableExtractor(), new AuthCookieManager(authTokenCookieNameGenerator));
        tokenProvider = new TokenProvider(jwtProperties);
        sut = new JwtTokenStrategy(jwtTokenExtractor, tokenProvider);
    }

    @DisplayName("요청값에서 토큰정보를 가져온다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void get_token_info_in_request(TokenType tokenType) {
        //given
        Long meetingId = 1L;
        Long participantId = 2L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookieName = authTokenCookieNameGenerator.generateCookieName(tokenType, meetingId);
        String token = tokenProvider.issueToken(participantId, TokenType.ACCESS_TOKEN);
        request.setCookies(new Cookie(cookieName, token));
        settingPathVariable(request, meetingId);

        //when
        TokenInfo tokenInfo = sut.process(request, tokenType);

        //then
        Assertions.assertThat(tokenInfo).isEqualTo(new TokenInfo(token, participantId));
    }

    private void settingPathVariable(MockHttpServletRequest request, Long meetingId) {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", meetingId + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
    }
}