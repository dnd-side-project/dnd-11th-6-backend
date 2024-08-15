package com.dnd.snappy.controller.v1.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.domain.auth.service.AuthTokenCookieNameGenerator;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.entity.RefreshToken;
import com.dnd.snappy.domain.token.repository.RefreshTokenRedisRepository;
import com.dnd.snappy.domain.token.service.JwtProperties;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthControllerTest extends RestDocsSupport {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @DisplayName("refreshToken을 통해 토큰을 재발급한다.")
    @Test
    void reissue_token() throws Exception {
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        refreshTokenRedisRepository.save(RefreshToken.builder().token(tokens.refreshToken()).id(participant.getId()).ttl(3600L).build());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String refreshTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.REFRESH_TOKEN, meeting.getId());

        mockMvc.perform(
                post("/api/v1/meetings/{meetingId}/tokens/refresh", meeting.getId())
                        .cookie(new Cookie(refreshTokenCookieName, tokens.refreshToken()))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName(refreshTokenCookieName).description("재발급을 위한 refresh token")
                                ),
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                responseCookies(
                                        cookieWithName(accessTokenCookieName).description("인증에 사용되는 access token"),
                                        cookieWithName(refreshTokenCookieName).description("재발급을 위한 refresh token")
                                )
                        )
                );
    }

    @DisplayName("요청 쿠키로 refresh token이 없다면 실패한다.")
    @Test
    void reissue_token_with_no_refresh_token_is_fail() throws Exception {
        Meeting meeting = createMeeting();

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/tokens/refresh", meeting.getId())
                )
                .andExpect(status().isUnauthorized())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("요청 쿠키로 유효기간이 끝난 refresh token을 준다면 실패한다.")
    @Test
    void reissue_token_with_expired_refresh_token_is_fail() throws Exception {
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting);
        TokenProvider fakeTokenProvider = new TokenProvider(new JwtProperties(jwtSecretKey, 1L, 1L));
        Tokens tokens = fakeTokenProvider.issueTokens(participant.getId());
        String refreshTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.REFRESH_TOKEN, meeting.getId());

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/tokens/refresh", meeting.getId())
                                .cookie(new Cookie(refreshTokenCookieName, tokens.refreshToken()))
                )
                .andExpect(status().isUnauthorized())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("참가자가 속하지 않은 모임에 재발급 요청시 실패한다.")
    @Test
    void reissue_token_with_other_meeting_refresh_token_is_fail() throws Exception {
        Meeting meeting = createMeeting();
        Meeting meeting2 = createMeeting();
        Participant participant = createParticipant(meeting);
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String refreshTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.REFRESH_TOKEN, meeting2.getId());
        refreshTokenRedisRepository.save(RefreshToken.builder().id(participant.getId()).token(tokens.refreshToken()).ttl(3600L).build());

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/tokens/refresh", meeting2.getId())
                                .cookie(new Cookie(refreshTokenCookieName, tokens.refreshToken()))
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    private ResponseFieldsSnippet getErrorResponseFields() {
        return responseFields(
                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                fieldWithPath("data").type(JsonFieldType.NULL).description("데이터"),
                fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
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