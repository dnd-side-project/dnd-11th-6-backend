package com.dnd.snappy.domain.participant.service;

import static org.mockito.BDDMockito.*;

import com.dnd.snappy.domain.participant.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.service.TokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @InjectMocks
    private ParticipationService participationService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private TokenService tokenService;

    @DisplayName("모임에 참여하는 참가자를 만든후 토큰을 생성한다.")
    @Test
    void participate() {
        //given
        Long participantId = 1L;
        Long meetingId = 2L;
        String nickname = "nickname";
        Role role = Role.LEADER;
        Tokens tokens = new Tokens("accessToken", "refreshToken");
        given(participantService.createParticipant(meetingId, nickname, role)).willReturn(participantId);
        given(tokenService.createTokens(participantId)).willReturn(tokens);

        //when
        ParticipationResponseDto result = participationService.participate(meetingId, nickname, role);

        //then
        Assertions.assertThat(result).isEqualTo(new ParticipationResponseDto(participantId, tokens.accessToken(), tokens.refreshToken()));
        verify(participantService, timeout(1)).createParticipant(meetingId, nickname, role);
        verify(tokenService, timeout(1)).createTokens(participantId);
    }
}