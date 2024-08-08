package com.dnd.snappy.domain.token.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.entity.RefreshToken;
import com.dnd.snappy.domain.token.repository.RefreshTokenRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @DisplayName("토큰을 생성하고 redis에 저장한다.")
    @Test
    void createTokens() {
        //given
        Long memberId = 1L;
        Tokens tokens = new Tokens("accessToken", "refreshToken");
        given(tokenProvider.issueTokens(memberId)).willReturn(tokens);

        //when
        Tokens result = tokenService.createTokens(memberId);

        //then
        Assertions.assertThat(result).isEqualTo(tokens);
        verify(refreshTokenRedisRepository, times(1)).save(any(RefreshToken.class));
    }
}