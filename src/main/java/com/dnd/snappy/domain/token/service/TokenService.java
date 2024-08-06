package com.dnd.snappy.domain.token.service;

import com.dnd.snappy.domain.token.dto.Tokens;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;

    public Tokens createTokens(Long memberId) {
        Tokens tokens = tokenProvider.issueTokens(memberId);
        //TODO: refreshToken redis에 저장

        return tokens;
    }
}
