package com.dnd.snappy.domain.member.service;

import static org.mockito.BDDMockito.*;

import com.dnd.snappy.domain.member.dto.response.ParticipationResponseDto;
import com.dnd.snappy.domain.member.entity.Role;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.service.TokenService;
import java.util.Optional;
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
    private MemberService memberService;

    @Mock
    private ParticipantService participantService;

    @Mock
    private TokenService tokenService;

    @DisplayName("accessToken없이 모임에 참여할때 member를 생성한다.")
    @Test
    void participate_when_access_token_is_null() {
        //given
        Optional<String> accessToken = Optional.empty();
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nickname";
        Role role = Role.LEADER;
        Tokens tokens = new Tokens("accessToken", "refreshToken");

        given(memberService.createMember()).willReturn(memberId);
        given(tokenService.createTokens(memberId)).willReturn(tokens);

        //when
        ParticipationResponseDto result = participationService.participate(accessToken, meetingId, nickname, role);

        //then
        Assertions.assertThat(result).isEqualTo(new ParticipationResponseDto(memberId, tokens.accessToken(), tokens.refreshToken()));
        verify(participantService, timeout(1)).joinMeeting(memberId, meetingId, nickname, role);
    }

    @DisplayName("accessToken가 존재할때 모임에 참여시 member를 생성하지 않는다.")
    @Test
    void participate_when_access_token_is_exist() {
        //given
        Optional<String> accessToken = Optional.of("token");
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nickname";
        Role role = Role.LEADER;
        Tokens tokens = new Tokens("accessToken", "refreshToken");

        given(tokenService.extractTokenIgnoringExpiration(accessToken.get())).willReturn(memberId);
        given(tokenService.createTokens(memberId)).willReturn(tokens);

        //when
        ParticipationResponseDto result = participationService.participate(accessToken, meetingId, nickname, role);

        //then
        Assertions.assertThat(result).isEqualTo(new ParticipationResponseDto(memberId, tokens.accessToken(), tokens.refreshToken()));
        verify(memberService, times(0)).createMember();
        verify(participantService, timeout(1)).joinMeeting(memberId, meetingId, nickname, role);
    }
}