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

    public Tokens createTokens(Long memberId) {
        Tokens tokens = tokenProvider.issueTokens(memberId);
        refreshTokenRedisRepository.save(RefreshToken.create(memberId, tokens.refreshToken(), REFRESH_TOKEN_TTL));
        return tokens;
    }

    public Long extractTokenIgnoringExpiration(String token) {
        return tokenProvider.extractPayloadIgnoringExpiration(token);
    }
}
