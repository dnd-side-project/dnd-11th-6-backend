package com.dnd.snappy.controller.v1.auth;

import static com.dnd.snappy.domain.token.service.TokenType.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthTokenCookieNameGeneratorTest {

    @DisplayName("인증 쿠키에 사용할 name을 생성한다.")
    @Test
    void generate_cookie_name_for_auth_cookie() {
        //given
        AuthTokenCookieNameGenerator sut = new AuthTokenCookieNameGenerator();

        //when
        String result = sut.generateCookieName(ACCESS_TOKEN, 1L);

        //then
        Assertions.assertThat(result).isEqualTo(ACCESS_TOKEN.getValue() + "_1");
    }
}