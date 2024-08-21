package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.domain.auth.service.AuthTokenCookieNameGenerator;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.dto.Tokens;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class MissionDetailControllerTest extends RestDocsSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthTokenCookieNameGenerator authTokenCookieNameGenerator;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionParticipantRepository missionParticipantRepository;

    @DisplayName("모임 리더가 미션 목록을 조회한다.")
    @Test
    void findLeaderMeetingMissions() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);
        Participant participant = createParticipant(meeting, Role.PARTICIPANT);

        Mission mission = Mission.builder()
                .meeting(meeting)
                .content("미션 내용")
                .build();
        missionRepository.save(mission);

        MissionParticipant missionParticipant = MissionParticipant.builder()
                .participant(participant)
                .mission(mission)
                .build();
        missionParticipantRepository.save(missionParticipant);

        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        get("/api/v1/meetings/{meetingId}/missions/leader", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data[].missionId").type(JsonFieldType.NUMBER).description("미션 ID"),
                                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("미션 내용"),
                                        fieldWithPath("data[].hasParticipants").type(JsonFieldType.BOOLEAN).description("참여자 여부")
                                )
                        )
                );
    }

    @DisplayName("모임 참가자가 미션 목록을 조회한다.")
    @Test
    void findMeetingMissions() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.PARTICIPANT);

        Mission mission = Mission.builder()
                .meeting(meeting)
                .content("미션 내용")
                .build();
        missionRepository.save(mission);

        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        get("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data[].missionId").type(JsonFieldType.NUMBER).description("미션 ID"),
                                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("미션 내용")
                                )
                        )
                );
    }

    @DisplayName("참여하지 않은 모임 ID로 미션 목록을 조회할 때 예외가 발생한다.")
    @Test
    void findMeetingMissions_MeetingNotFound() throws Exception {
        // given
        Long nonExistentMeetingId = 999L;
        Participant participant = createParticipant(createMeeting(), Role.PARTICIPANT);

        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, nonExistentMeetingId);
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        get("/api/v1/meetings/{meetingId}/missions", nonExistentMeetingId)
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("리더가 아닌 사용자가 리더 전용 미션 목록을 조회할 때 예외가 발생한다.")
    @Test
    void findLeaderMeetingMissions_UNAUTHORIZED_MISSION() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.PARTICIPANT);

        Mission mission = Mission.builder()
                .meeting(meeting)
                .content("미션 내용")
                .build();
        missionRepository.save(mission);

        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        get("/api/v1/meetings/{meetingId}/missions/leader", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    private Meeting createMeeting() {
        Meeting meeting = Meeting.builder()
                .name("DND")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .symbolColor("#fff")
                .meetingLink("link")
                .password("1234")
                .leaderAuthKey("1123")
                .build();
        return meetingRepository.save(meeting);
    }

    private Participant createParticipant(Meeting meeting, Role role) {
        Participant participant = Participant.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .meeting(meeting)
                .nickname("nick")
                .role(role)
                .shootCount(10)
                .build();
        return participantRepository.save(participant);
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
}
