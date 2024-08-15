package com.dnd.snappy.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.dto.response.ReissueTokenResponseDto;
import com.dnd.snappy.domain.auth.exception.AuthErrorCode;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.entity.RefreshToken;
import com.dnd.snappy.domain.token.repository.RefreshTokenRedisRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthService sut;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("토큰을 재발급한다.")
    @Test
    void reissueTokens() {
        //given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.REFRESH_TOKEN);
        refreshTokenRedisRepository.save(RefreshToken.builder().token(token).ttl(3600L).id(participant.getId()).build());

        //when
        ReissueTokenResponseDto result = sut.reissueTokens(meeting.getId(), participant.getId(), token);

        //then
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.meetingExpiredDate()).isEqualTo(meeting.getExpiredDate());
        assertThat(refreshTokenRedisRepository.findById(participant.getId()).orElse(null)).isNotNull();
    }

    @DisplayName("redis에 저장된 토큰과 매개변수로 준 토큰이 서로 다르다면면 예외가 발생한다.")
    @Test
    void not_same_redis_token_throw_exception() {
        //given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.REFRESH_TOKEN);
        refreshTokenRedisRepository.save(RefreshToken.builder().token("other token").ttl(3600L).id(participant.getId()).build());

        //when & then
        assertThatThrownBy(() -> sut.reissueTokens(meeting.getId(), participant.getId(), token))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(AuthErrorCode.FORBIDDEN.getMessage())
                .hasMessageContaining("유효하지 않은 토큰입니다.");

    }

    @DisplayName("자신이 참여하고 있지 않은 모임에 대해 토큰 재발급을 하면 예외가 발생한다.")
    @Test
    void not_participation_meeting_throw_exception() {
        //given
        Meeting meeting1 = createMeeting();
        Meeting meeting2 = createMeeting();
        Participant participant = createParticipant(meeting1);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.REFRESH_TOKEN);
        refreshTokenRedisRepository.save(RefreshToken.builder().token(token).ttl(3600L).id(participant.getId()).build());

        //when & then
        assertThatThrownBy(() -> sut.reissueTokens(meeting2.getId(), participant.getId(), token))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(AuthErrorCode.FORBIDDEN.getMessage())
                .hasMessageContaining("참여중인 모임이 아닙니다.");

    }

    private Meeting createMeeting() {
        Meeting meeting = Meeting.builder()
                .name("name")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .symbolColor("#fff")
                .meetingLink("link")
                .password("password")
                .leaderAuthKey("aaa")
                .build();
        return meetingRepository.save(meeting);
    }

    private Participant createParticipant(Meeting meeting) {
        Participant participant = Participant.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .meeting(meeting)
                .nickname("nick")
                .role(Role.LEADER)
                .shootCount(10)
                .build();
        return participantRepository.save(participant);
    }
}