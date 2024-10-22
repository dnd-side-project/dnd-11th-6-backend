package com.dnd.snappy.domain.token.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refreshToken")
public class RefreshToken {
    @Id
    private Long id;

    private String token;

    @TimeToLive
    private Long ttl;

    @Builder
    private RefreshToken(Long id, String token, Long ttl) {
        this.id = id;
        this.token = token;
        this.ttl = ttl;
    }

    public static RefreshToken create(Long id, String token, Long ttl) {
        return RefreshToken.builder()
                .id(id)
                .token(token)
                .ttl(ttl)
                .build();
    }

    public boolean equalsToken(String token) {
        return this.token.equals(token);
    }
}
