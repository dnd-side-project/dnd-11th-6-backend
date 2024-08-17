package com.dnd.snappy.controller.v1.participant;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.controller.v1.participant.request.ParticipationRequest;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.service.JwtProperties;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ParticipantControllerTest extends RestDocsSupport {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @DisplayName("사용자는 모임에 참여할 수 있다.")
    @Test
    void participateMeeting() throws Exception {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        ParticipationRequest participationRequest = new ParticipationRequest("123456", Role.LEADER);

        //when & then
        mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/participants", meeting.getId())
                        .content(objectMapper.writeValueAsString(participationRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("모임에 사용할 닉네임"),
                                        fieldWithPath("role").type(JsonFieldType.STRING).attributes(key("format").value("LEADER(리더) | PARTICIPANT(참가자)")).description("사용자 권한")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.participantId").type(JsonFieldType.NUMBER).description("참여자 id")
                                ),
                                responseCookies(
                                        cookieWithName("ACCESS_TOKEN_"  +meeting.getId()).description("인증에 사용되는 accessToken"),
                                        cookieWithName("REFRESH_TOKEN_"  +meeting.getId()).description("accessToken 재발급을 위한 refreshToken")
                                )
                        )
                );
    }

    @DisplayName("참여하기 원하는 모임이 없다면 예외가 발생한다.")
    @Test
    void participate_not_exist_meeting() throws Exception {
        //given
        ParticipationRequest participationRequest = new ParticipationRequest("123456", Role.LEADER);

        //when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/participants", 1L)
                                .content(objectMapper.writeValueAsString(participationRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("모임에 중복된 닉네임이 있다면 예외가 발생한다.")
    @Test
    void duplicated_nickname_in_meeting_throw_exception() throws Exception {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        appendParticipant(meeting, "nick");

        ParticipationRequest participationRequest = new ParticipationRequest("nick", Role.LEADER);

        //when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/participants", meeting.getId())
                                .content(objectMapper.writeValueAsString(participationRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("모임이 끝났을때 참여하면 예외가 발생한다.")
    @Test
    void join_finish_meeting_throw_exception() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusNanos(1));
        ParticipationRequest participationRequest = new ParticipationRequest("123456", Role.LEADER);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/participants", meeting.getId())
                                .content(objectMapper.writeValueAsString(participationRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("로그인한 참가자의 세부정보를 조회한다.")
    @Test
    void getParticipantInMeeting() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(3);
        Meeting meeting = appendMeeting(startDate, endDate);
        Participant participant = appendParticipant(meeting, "재훈");
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);

        mockMvc.perform(
                        get("/api/v1/meetings/{meetingId}/participants/me", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증에 사용되는 access token")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("참가자 세부정보"),
                                        fieldWithPath("data.participantId").type(JsonFieldType.NUMBER).description("참가자 ID"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("참가자 닉네임"),
                                        fieldWithPath("data.role").type(JsonFieldType.STRING).attributes(key("format").value("LEADER(리더) | PARTICIPANT(참가자)")).description("참가자 권한"),
                                        fieldWithPath("data.shootCount").type(JsonFieldType.NUMBER).description("참가자 촬영 횟수")
                                )
                        )
                );
    }

    @DisplayName("로그인한 참가자의 인증 토큰이 만료된 경우 예외가 발생한다.")
    @Test
    void getParticipantInMeeting_expired_token() throws Exception {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(3);
        Meeting meeting = appendMeeting(startDate, endDate);
        Participant participant = appendParticipant(meeting, "재훈");
        TokenProvider fakeTokenProvider = new TokenProvider(new JwtProperties(jwtSecretKey, 1L, 1L));
        String token = fakeTokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);

        mockMvc.perform(
                        get("/api/v1/meetings/{meetingId}/participants/me", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
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


    private Meeting appendMeeting(LocalDateTime startDate, LocalDateTime endDate) {
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(startDate)
                .endDate(endDate)
                .meetingLink("meetingLink")
                .password("password")
                .leaderAuthKey("adminPassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return meetingRepository.save(meeting);
    }

    private Participant appendParticipant(Meeting meeting, String nickname) {
        Participant participant = Participant.builder()
                .nickname(nickname)
                .role(Role.LEADER)
                .shootCount(10)
                .meeting(meeting)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        return participantRepository.save(participant);
    }
}