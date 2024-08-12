package com.dnd.snappy.domain.token.service;

import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.entity.RefreshToken;
import com.dnd.snappy.domain.token.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    public final static Long REFRESH_TOKEN_TTL = 3600L; //1시간

    private final TokenProvider tokenProvider;

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public Tokens createTokens(Long participantId) {
        Tokens tokens = tokenProvider.issueTokens(participantId);
        refreshTokenRedisRepository.save(RefreshToken.create(participantId, tokens.refreshToken(), REFRESH_TOKEN_TTL));
        return tokens;
    }
}
