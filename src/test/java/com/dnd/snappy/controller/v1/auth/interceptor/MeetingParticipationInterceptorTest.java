package com.dnd.snappy.controller.v1.auth.interceptor;

import static com.dnd.snappy.domain.auth.exception.AuthErrorCode.UNAUTHORIZED;
import static com.dnd.snappy.domain.participant.exception.ParticipantErrorCode.NOT_PARTICIPATING_MEETING;
import static com.dnd.snappy.domain.token.exception.TokenErrorCode.JWT_EXTRACT_ERROR;
import static org.assertj.core.api.Assertions.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.domain.auth.service.AuthTokenCookieNameGenerator;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.IntegrationTestSupport;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerMapping;


@Transactional
class MeetingParticipationInterceptorTest extends IntegrationTestSupport {

    @Autowired
    private MeetingParticipationInterceptor meetingParticipationInterceptor;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("특정 미팅에 속한 참가자가 인증 쿠키를 가지고 요청을 보내면 true를 리턴한다.")
    @Test
    void participant_in_meeting_with_auth_cookie_is_true() {
        //given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        MockHttpServletRequest request = new MockHttpServletRequest();
        settingRequest(request, meeting, participant);

        //when
        boolean result = meetingParticipationInterceptor.preHandle(request, null, null);

        //then
        assertThat(result).isTrue();

    }

    @DisplayName("미팅 참가 요청은 무조건 성공한다.")
    @ParameterizedTest
    @CsvSource({
            "/api/v1/meetings/1/participants",
            "/api/v2/meetings/1/participants",
            "/api/v3/meetings/1/participants",
            "/api/v4/meetings/1/participants"
    })
    void participant_request_is_true() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/meetings/1/participants");
        request.setMethod("POST");

        //when
        boolean result = meetingParticipationInterceptor.preHandle(request, null, null);

        //then
        assertThat(result).isTrue();

    }

    @DisplayName("특정 미팅에 속하지 않은 참가자가 인증 쿠키를 가지고 요청을 보내면 예외가 발생한다.")
    @Test
    void participant_in_meeting_with_invalid_auth_cookie_is_false() {
        //given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", (meeting.getId() + 1) + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        request.setCookies(new Cookie(
                        authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId()),
                        tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN)
                )
        );

        //when & then
        assertThatThrownBy(() -> meetingParticipationInterceptor.preHandle(request, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(UNAUTHORIZED.getMessage());

    }

    @DisplayName("참가자가 유효하지 않은 토큰을 가지고 요청을 보내면 예외가 발생한다.")
    @Test
    void participant_with_invalid_token_throw_exception() {
        //given
        Meeting meeting = createMeeting();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", meeting.getId() + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        request.setCookies(new Cookie(
                        authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId()),
                        "invalid_token"
                )
        );

        //when & then
        assertThatThrownBy(() -> meetingParticipationInterceptor.preHandle(request, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(JWT_EXTRACT_ERROR.getMessage());

    }

    @DisplayName("특정 미팅에 속하지 않은 참가자가 요청을 보내면 예외가 발생한다.")
    @Test
    void participant_no_meeting_throw_exception() {
        //given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", (meeting.getId() + 1) + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        request.setCookies(new Cookie(
                        authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId()+1),
                        tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN)
                )
        );

        //when & then
        assertThatThrownBy(() -> meetingParticipationInterceptor.preHandle(request, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(NOT_PARTICIPATING_MEETING.getMessage());

    }

    private void settingRequest(MockHttpServletRequest request, Meeting meeting, Participant participant) {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", meeting.getId() + "");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        request.setCookies(new Cookie(
                    authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId()),
                    tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN)
                )
        );
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