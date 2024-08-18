package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.domain.auth.service.AuthTokenCookieNameGenerator;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.request.ModifyMissionRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class MissionControllerTest extends RestDocsSupport {

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
    private MissionParticipantRepository missionParticipantRepository;

    @Autowired
    private MissionRepository missionRepository;

    @DisplayName("모임 리더는 미션을 생성한다.")
    @Test
    void createMission() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.LEADER);

        CreateMissionRequestDto requestDto = new CreateMissionRequestDto("모임 미션 내용");
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(requestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("미션 내용")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("미션 내용")
                                )
                        )
                );
    }

    @DisplayName("모임 리더는 미션을 수정한다.")
    @Test
    void modifyMission() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);

        CreateMissionRequestDto createRequestDto = new CreateMissionRequestDto("수정 전 미션 내용");
        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(createRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Mission createdMission = missionRepository.findAll().get(0);
        Long missionId = createdMission.getId();

        ModifyMissionRequestDto modifyRequestDto = new ModifyMissionRequestDto("수정된 미션 내용");

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID"),
                                        parameterWithName("missionId").description("미션 ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정된 미션 내용")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("수정된 미션 내용")
                                )
                        )
                );
    }

    @DisplayName("모임 리더는 미션을 삭제한다.")
    @Test
    void deleteMission() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);

        CreateMissionRequestDto createRequestDto = new CreateMissionRequestDto("삭제 전 미션 내용");
        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(createRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Mission createdMission = missionRepository.findAll().get(0);
        Long missionId = createdMission.getId();

        // when & then
        mockMvc.perform(
                        delete("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID"),
                                        parameterWithName("missionId").description("미션 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("성공 메시지")
                                )
                        )
                );
    }

    @DisplayName("유효하지 않은 입력 데이터로 미션 생성 시 실패한다.")
    @Test
    void createMission_INVALID_INPUT() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);

        List<CreateMissionRequestDto> invalidRequestDtos = List.of(
                new CreateMissionRequestDto(""),
                new CreateMissionRequestDto("ab"),
                new CreateMissionRequestDto("a".repeat(21))
        );

        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        for (CreateMissionRequestDto requestDto : invalidRequestDtos) {
            // when & then
            mockMvc.perform(
                            post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                    .cookie(new Cookie(accessTokenCookieName, accessToken))
                                    .content(objectMapper.writeValueAsString(requestDto))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andDo(
                            restDocs.document(
                                    getErrorResponseFields()
                            )
                    );
        }
    }

    @DisplayName("모임 리더가 아닌 사용자가 미션을 생성하려 할 때 실패한다.")
    @Test
    void createMission_UNAUTHORIZED_MISSION() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.PARTICIPANT);
        CreateMissionRequestDto requestDto = new CreateMissionRequestDto("모임 미션 내용");
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(requestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("모임 리더가 아닌 사용자가 미션 수정하려 할 때 실패한다.")
    @Test
    void modifyMission_UNAUTHORIZED_MISSION() throws Exception {
        // given
        Meeting meeting = createMeeting();

        Participant leader = createParticipant(meeting, Role.LEADER);
        Tokens leaderTokens = tokenProvider.issueTokens(leader.getId());
        String leaderAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String leaderAccessToken = leaderTokens.accessToken();

        CreateMissionRequestDto createRequestDto = new CreateMissionRequestDto("미션 내용");

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(leaderAccessTokenCookieName, leaderAccessToken))
                                .content(objectMapper.writeValueAsString(createRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Mission createdMission = missionRepository.findAll().get(0);
        Long missionId = createdMission.getId();

        Participant participant = createParticipant(meeting, Role.PARTICIPANT);
        Tokens participantTokens = tokenProvider.issueTokens(participant.getId());
        String participantAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String participantAccessToken = participantTokens.accessToken();

        ModifyMissionRequestDto modifyRequestDto = new ModifyMissionRequestDto("수정된 미션 내용");

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(participantAccessTokenCookieName, participantAccessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("모임 리더가 아닌 사용자가 미션 삭제하려 할 때 실패한다.")
    @Test
    void deleteMission_UNAUTHORIZED_MISSION() throws Exception {
        // given
        Meeting meeting = createMeeting();

        Participant leader = createParticipant(meeting, Role.LEADER);
        Tokens leaderTokens = tokenProvider.issueTokens(leader.getId());
        String leaderAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String leaderAccessToken = leaderTokens.accessToken();

        CreateMissionRequestDto createRequestDto = new CreateMissionRequestDto("미션 내용");

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(leaderAccessTokenCookieName, leaderAccessToken))
                                .content(objectMapper.writeValueAsString(createRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Mission createdMission = missionRepository.findAll().get(0);
        Long missionId = createdMission.getId();

        Participant participant = createParticipant(meeting, Role.PARTICIPANT);
        Tokens participantTokens = tokenProvider.issueTokens(participant.getId());
        String participantAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String participantAccessToken = participantTokens.accessToken();

        // when & then
        mockMvc.perform(
                        delete("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(participantAccessTokenCookieName, participantAccessToken))
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("존재하지 않는 미션을 수정하려 할 때 실패한다.")
    @Test
    void modifyMission_MISSION_NOT_FOUND() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.LEADER);

        Long missionId = 999L;
        ModifyMissionRequestDto modifyRequestDto = new ModifyMissionRequestDto("수정된 미션 내용");
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("존재하지 않는 미션을 삭제하려 할 때 실패한다.")
    @Test
    void deleteMission_MISSION_NOT_FOUND() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.LEADER);

        Long missionId = 999L;
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        delete("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                )
                .andExpect(status().isNotFound())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("미션에 참여자가 있어 수정할 수 없는 경우 실패한다.")
    @Test
    void modifyMission_MISSION_HAS_PARTICIPANTS() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);
        Participant participant = createParticipant(meeting, Role.PARTICIPANT);

        Mission mission = Mission.builder()
                .meeting(meeting)
                .content("미션 내용")
                .build();
        missionRepository.save(mission);

        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        MissionParticipant missionParticipant = MissionParticipant.builder()
                .participant(participant)
                .mission(mission)
                .build();
        missionParticipantRepository.save(missionParticipant);

        ModifyMissionRequestDto modifyRequestDto = new ModifyMissionRequestDto("수정된 미션 내용");

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), mission.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                getErrorResponseFields()
                        )
                );
    }

    @DisplayName("미션 내용을 변경하지 않고 수정 요청 시 실패한다.")
    @Test
    void modifyMission_MISSION_CONTENT_UNCHANGED() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant participant = createParticipant(meeting, Role.LEADER);

        Long missionId = 1L;
        CreateMissionRequestDto createRequestDto = new CreateMissionRequestDto("수정 전 미션 내용");
        Tokens tokens = tokenProvider.issueTokens(participant.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        mockMvc.perform(
                        post("/api/v1/meetings/{meetingId}/missions", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(createRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        ModifyMissionRequestDto modifyRequestDto = new ModifyMissionRequestDto("수정 전 미션 내용");

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}/missions/{missionId}", meeting.getId(), missionId)
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID"),
                                        parameterWithName("missionId").description("미션 ID")
                                ),
                                requestFields(
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("수정된 미션 내용")
                                ),
                                getErrorResponseFields()
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
