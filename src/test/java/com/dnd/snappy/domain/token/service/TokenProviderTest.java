package com.dnd.snappy.domain.token.service;

import static org.assertj.core.api.Assertions.*;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.token.dto.Tokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TokenProviderTest {

    private final String testSecretKey = "test-secret-test-secret-test-secret-test-secret";
    private final JwtProperties jwtProperties = new JwtProperties(testSecretKey, 18000L, 36000L);

    @DisplayName("access토큰과 refresh토큰을 발급한다.")
    @Test
    void issueTokens() {
        //given
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);

        //when
        Tokens tokens = tokenProvider.issueTokens(1L);

        //then
        assertThatCode(() -> tokenProvider.extractPayload(tokens.accessToken()))
                .doesNotThrowAnyException();
        assertThatCode(() -> tokenProvider.extractPayload(tokens.refreshToken()))
                .doesNotThrowAnyException();
    }

    @DisplayName("토큰을 추출하여 participantId를 가져온다.")
    @Test
    void extractPayload() {
        //given
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);
        String token = tokenProvider.issueToken(1L, TokenType.ACCESS_TOKEN);

        //when
        Long participantId = tokenProvider.extractPayload(token);

        //then
        assertThat(participantId).isEqualTo(1L);
    }

    @DisplayName("토큰을 추출힐때 토큰이 만료되었다면 예외가 발생한다.")
    @Test
    void extractPayloadWhenTokenIsExpired() {
        //given
        TokenProvider tokenProvider = new TokenProvider(new JwtProperties(testSecretKey, 1L, 1L));
        String token = tokenProvider.issueToken(1L, TokenType.ACCESS_TOKEN);

        //when //then
        assertThatThrownBy(() -> tokenProvider.extractPayload(token))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.JWT_EXPIRED_ERROR.getMessage());
    }

    @DisplayName("토큰을 추출힐때, 토큰이 비어있다면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void extractPayloadWhenTokenIsNullAndEmpty(String token) {
        //given
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);

        //when //then
        assertThatThrownBy(() -> tokenProvider.extractPayload(token))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.JWT_EXTRACT_ERROR.getMessage());
    }

    @DisplayName("토큰을 추출힐때, 유효하지 않은 토큰이라면 예외를 발생시킨다.")
    @Test
    void extractPayloadWhenTokenIsInvalid() {
        //given
        String token = "invalid-token";
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);

        //when //then
        assertThatThrownBy(() -> tokenProvider.extractPayload(token))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.JWT_EXTRACT_ERROR.getMessage());
    }

    @DisplayName("토큰을 추출하여 participantId를 가져온다.")
    @Test
    void extractPayloadIgnoringExpiration() {
        //given
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);
        String token = tokenProvider.issueToken(1L, TokenType.ACCESS_TOKEN);

        //when
        Long participantId = tokenProvider.extractPayloadIgnoringExpiration(token);

        //then
        assertThat(participantId).isEqualTo(1L);
    }

    @DisplayName("토큰의 만료기한이 지났더라도 participantId를 가져온다.")
    @Test
    void extractPayloadIgnoringExpiration_when_token_is_expired() {
        //given
        TokenProvider tokenProvider = new TokenProvider(new JwtProperties(testSecretKey, 1L, 1L));
        String token = tokenProvider.issueToken(1L, TokenType.ACCESS_TOKEN);

        //when
        Long participantId = tokenProvider.extractPayloadIgnoringExpiration(token);

        //then
        assertThat(participantId).isEqualTo(1L);
    }

    @DisplayName("토큰을 추출힐때, 유효하지 않은 토큰이라면 예외를 발생시킨다.")
    @Test
    void extractPayloadIgnoringExpiration_when_token_is_invalid() {
        //given
        String token = "invalid-token";
        TokenProvider tokenProvider = new TokenProvider(jwtProperties);

        //when //then
        assertThatThrownBy(() -> tokenProvider.extractPayloadIgnoringExpiration(token))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.JWT_EXTRACT_ERROR.getMessage());
    }

}