package com.dnd.snappy.domain.auth.service;

import static org.assertj.core.api.Assertions.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.exception.AuthErrorCode;
import com.dnd.snappy.domain.token.exception.TokenErrorCode;
import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

class JwtTokenExtractorTest {

    private AuthTokenCookieNameGenerator authTokenCookieNameGenerator;
    private AuthCookieManager authCookieManager;
    private JwtTokenExtractor sut;
    private PathVariableExtractor pathVariableExtractor;

    @BeforeEach
    void setUp() {
        authTokenCookieNameGenerator = new AuthTokenCookieNameGenerator();
        pathVariableExtractor = new PathVariableExtractor();
        authCookieManager = new AuthCookieManager(authTokenCookieNameGenerator);
        sut = new JwtTokenExtractor(pathVariableExtractor,authCookieManager);
    }

    @DisplayName("요청값에서 토큰을 추출한다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void extract_token(TokenType tokenType) {
        //given
        Long meetingId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookieName = authTokenCookieNameGenerator.generateCookieName(tokenType, meetingId);
        request.setCookies(new Cookie(cookieName, "token"));
        settingPathVariable(request, meetingId);

        //when
        String token = sut.extractToken(request, tokenType);

        //then
        assertThat(token).isEqualTo("token");
    }

    @DisplayName("요청에서 토큰을 추출 할 수 없다면 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void extract_token_with_invalid_cookie_name_throw_exception(TokenType tokenType) {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("invalidName", "token"));
        settingPathVariable(request, 1L);

        //when & then
        assertThatThrownBy(() -> sut.extractToken(request, tokenType))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(AuthErrorCode.UNAUTHORIZED.getMessage());

    }

    private void settingPathVariable(MockHttpServletRequest request, Long meetingId) {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", meetingId + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
    }
}