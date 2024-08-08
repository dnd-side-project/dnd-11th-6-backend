package com.dnd.snappy.controller.v1.member;

import com.dnd.snappy.domain.token.entity.RefreshToken;
import com.dnd.snappy.domain.token.repository.RefreshTokenRedisRepository;
import com.dnd.snappy.support.RestDocsSupport;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ParticipationControllerTest extends RestDocsSupport {

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @DisplayName("")
    @Test
    void test() {
        //givend
        refreshTokenRedisRepository.save(RefreshToken.create(1L, "token", 360L));

        //when

        //then

    }
}