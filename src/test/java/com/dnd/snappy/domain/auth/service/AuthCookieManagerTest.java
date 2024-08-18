package com.dnd.snappy.domain.auth.service;

import static org.assertj.core.api.Assertions.*;

import com.dnd.snappy.domain.token.service.TokenType;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;

class AuthCookieManagerTest {

    @DisplayName("인증 토큰을 위한 쿠키를 만든다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void create_cookie_for_token(TokenType tokenType) {
        //given
        AuthTokenCookieNameGenerator authTokenCookieNameGenerator = new AuthTokenCookieNameGenerator();
        AuthCookieManager sut = new AuthCookieManager(authTokenCookieNameGenerator);
        Duration duration = Duration.ofSeconds(10);

        //when
        String result = sut.createTokenCookie(tokenType, "token", 1L, duration);

        //then
        String name = authTokenCookieNameGenerator.generateCookieName(tokenType, 1L);
        ResponseCookie expectedCookie = ResponseCookie.from(name, "token")
                .httpOnly(true)
                .secure(false)
                .path("/api/")
                .sameSite("Strict")
                .maxAge(duration)
                .build();
        assertThat(result).isEqualTo(expectedCookie.toString());
    }

    @DisplayName("인증 쿠키를 요청에서 가져온다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void get_auth_cookie(TokenType tokenType) {
        //given
        AuthTokenCookieNameGenerator authTokenCookieNameGenerator = new AuthTokenCookieNameGenerator();
        AuthCookieManager sut = new AuthCookieManager(authTokenCookieNameGenerator);
        MockHttpServletRequest request = new MockHttpServletRequest();
        String name = authTokenCookieNameGenerator.generateCookieName(tokenType, 1L);
        Cookie cookie = new Cookie(name, "token");
        request.setCookies(cookie);

        //when
        Optional<Cookie> authCookie = sut.getAuthCookie(request, tokenType, 1L);

        //then
        assertThat(authCookie.isPresent()).isTrue();
        assertThat(authCookie.get().getValue()).isEqualTo("token");
    }

    @DisplayName("요청에 쿠키가 아예 없다면 빈값을 가져온다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void get_auth_cookie_empty(TokenType tokenType) {
        //given
        AuthTokenCookieNameGenerator authTokenCookieNameGenerator = new AuthTokenCookieNameGenerator();
        AuthCookieManager sut = new AuthCookieManager(authTokenCookieNameGenerator);
        MockHttpServletRequest request = new MockHttpServletRequest();

        //when
        Optional<Cookie> authCookie = sut.getAuthCookie(request, tokenType, 1L);

        //then
        assertThat(authCookie.isPresent()).isFalse();
    }

    @DisplayName("요청에 쿠키가 있지만 원하는 쿠키 이름이 없다면 빈값을 가져온다.")
    @ParameterizedTest
    @EnumSource(TokenType.class)
    void get_auth_cookie_name_not_found(TokenType tokenType) {
        //given
        AuthTokenCookieNameGenerator authTokenCookieNameGenerator = new AuthTokenCookieNameGenerator();
        AuthCookieManager sut = new AuthCookieManager(authTokenCookieNameGenerator);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("name", "1");
        request.setCookies(cookie);

        //when
        Optional<Cookie> authCookie = sut.getAuthCookie(request, tokenType, 1L);

        //then
        assertThat(authCookie.isPresent()).isFalse();
    }
}