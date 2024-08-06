package com.dnd.snappy.domain.token.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.entity.RefreshToken;
import com.dnd.snappy.domain.token.repository.RefreshTokenRedisRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final static Long REFRESH_TOKEN_TTL = 3600L; //1시간

    private final TokenProvider tokenProvider;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public Tokens createTokens(Long memberId) {
        Tokens tokens = tokenProvider.issueTokens(memberId);
        refreshTokenRedisRepository.save(RefreshToken.create(memberId, tokens.refreshToken(), REFRESH_TOKEN_TTL));
        return tokens;
    }

    public Optional<Long> extractToken(String token) {
        if(token != null) {
            try {
                Long memberId = tokenProvider.extractPayload(token);
                return Optional.of(memberId);
            } catch (BusinessException e) {
                //토큰 만료 된 경우
                // 토큰이 만료된 경우라면 memberId값 빼오기 이때 비밀번호도 맞는지 확인해줘야할듯
                // 단순히 memberId값만 빼온다면 공격자가
            }
        }

        return Optional.empty();
    }
}
