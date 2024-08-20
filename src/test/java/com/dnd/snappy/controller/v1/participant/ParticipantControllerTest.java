package com.dnd.snappy.controller.v1.participant;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.controller.v1.participant.request.ParticipationRequest;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.JwtProperties;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Random;
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
                .andExpect(status().isCreated())
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
        appendParticipant(meeting, "nick", Role.LEADER, 10);

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
        Participant participant = appendParticipant(meeting, "재훈", Role.LEADER, 10);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/participants/me", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증에 사용되는 access token")
                                ),
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                )
                                ,
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
        Participant participant = appendParticipant(meeting, "재훈", Role.LEADER, 10);
        TokenProvider fakeTokenProvider = new TokenProvider(new JwtProperties(jwtSecretKey, 1L, 1L));
        String token = fakeTokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/participants/me", meeting.getId())
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

    @DisplayName("닉네임이 사용가능한지 알 수 있다.")
    @Test
    void checkDuplicateNickname() throws Exception {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/participants/check-nickname", meeting.getId())
                                .queryParam("nickname", "nick")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("nickname").description("사용하고 싶은 닉네임")
                                )
                                ,
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                )
                                ,
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("닉네임 사용 가능한지"),
                                        fieldWithPath("data.isAvailableNickname").type(JsonFieldType.BOOLEAN).description("닉네임 사용 가능한지")
                                )
                        )
                );

    }

    @DisplayName("닉네임이 사용가능한지 알 수 있다.")
    @Test
    void checkDuplicateNickname_false() throws Exception {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        appendParticipant(meeting, "nick", Role.LEADER, 10);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/participants/check-nickname", meeting.getId())
                                .queryParam("nickname", "nick")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("nickname").description("사용하고 싶은 닉네임")
                                )
                                ,
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                )
                                ,
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("닉네임 사용 가능한지"),
                                        fieldWithPath("data.isAvailableNickname").type(JsonFieldType.BOOLEAN).description("닉네임 사용 가능한지")
                                )
                        )
                );

    }

    @DisplayName("모임내의 참여자들을 커서 기반 페이지네이션으로 조회한다.")
    @Test
    void findParticipantsInMeeting() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        Participant participant = appendParticipant(meeting, "nick1", Role.LEADER, 4);
        for(int i=2; i<=6; i++) {
            if(i < 4) appendParticipant(meeting, "nick" + i, Role.LEADER, i);
            else appendParticipant(meeting, "nick" + i, Role.PARTICIPANT, i);
        }
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);


        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/participants", meeting.getId())
                                .queryParam("cursorId", String.valueOf(0))
                                .queryParam("limit", String.valueOf(6))
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("cursorId").description("마지막으로 조회한 cursorId값"),
                                        parameterWithName("limit").description("조회하고 싶은 데이터 수 (기본: 10개)")
                                ),
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증에 사용되는 access token")
                                ),
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                )
                                ,
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.nextCursorId").type(JsonFieldType.NUMBER).description("다음 cursorId값"),
                                        fieldWithPath("data.data").type(JsonFieldType.ARRAY).description("참가자 데이터"),
                                        fieldWithPath("data.data[].participantId").type(JsonFieldType.NUMBER).description("참가자 ID"),
                                        fieldWithPath("data.data[].nickname").type(JsonFieldType.STRING).description("참가자 닉네임"),
                                        fieldWithPath("data.data[].role").type(JsonFieldType.STRING).attributes(key("format").value("LEADER(리더) | PARTICIPANT(참가자)")).description("참가자 권한"),
                                        fieldWithPath("data.data[].shootCount").type(JsonFieldType.NUMBER).description("참가자 촬영 횟수"),
                                        fieldWithPath("data.count").type(JsonFieldType.NUMBER).description("전체 참가자 수"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")

                                )
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

    private Participant appendParticipant(Meeting meeting, String nickname, Role role, int shootCount) {
        Participant participant = Participant.builder()
                .nickname(nickname)
                .role(role)
                .shootCount(shootCount)
                .meeting(meeting)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        return participantRepository.save(participant);
    }
}