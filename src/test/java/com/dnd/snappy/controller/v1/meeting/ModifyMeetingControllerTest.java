package com.dnd.snappy.controller.v1.meeting;

import com.dnd.snappy.domain.auth.service.AuthTokenCookieNameGenerator;
import com.dnd.snappy.domain.meeting.dto.request.ModifyMeetingRequestDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ModifyMeetingControllerTest extends RestDocsSupport {

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

    @DisplayName("모임 리더는 모임 정보를 수정한다.")
    @Test
    void modifyMeeting() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);

        ModifyMeetingRequestDto modifyRequestDto = new ModifyMeetingRequestDto("수정된 모임 이름", "수정된 모임 설명", "#000000");

        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}", meeting.getId())
                                .cookie(new Cookie(accessTokenCookieName, accessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("수정된 모임 이름").optional(),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("수정된 모임 설명").optional(),
                                        fieldWithPath("symbolColor").type(JsonFieldType.STRING).description("수정된 모임 색상").optional()
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data.meetingId").type(JsonFieldType.NUMBER).description("모임 ID"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("수정된 모임 이름"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("수정된 모임 설명"),
                                        fieldWithPath("data.symbolColor").type(JsonFieldType.STRING).description("수정된 모임 색상")
                                )
                        )
                );
    }

    @DisplayName("유효하지 않은 입력 데이터로 모임 수정 시 실패한다.")
    @Test
    void modifyMeeting_INVALID_INPUT() throws Exception {
        // given
        Meeting meeting = createMeeting();
        Participant leader = createParticipant(meeting, Role.LEADER);

        List<ModifyMeetingRequestDto> invalidRequestDtos = List.of(
                new ModifyMeetingRequestDto("", null, null),
                new ModifyMeetingRequestDto("ab", "설명", "#000000"),
                new ModifyMeetingRequestDto("a".repeat(101), "설명", "#000000")
        );

        Tokens tokens = tokenProvider.issueTokens(leader.getId());
        String accessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String accessToken = tokens.accessToken();

        for (ModifyMeetingRequestDto requestDto : invalidRequestDtos) {
            // when & then
            mockMvc.perform(
                            patch("/api/v1/meetings/{meetingId}", meeting.getId())
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

    @DisplayName("모임 리더가 아닌 사용자가 모임 정보를 수정하려 할 때 실패한다.")
    @Test
    void modifyMeeting_UNAUTHORIZED_MEETING() throws Exception {
        // given
        Meeting meeting = createMeeting();

        Participant leader = createParticipant(meeting, Role.LEADER);
        Tokens leaderTokens = tokenProvider.issueTokens(leader.getId());
        String leaderAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String leaderAccessToken = leaderTokens.accessToken();

        ModifyMeetingRequestDto modifyRequestDto = new ModifyMeetingRequestDto("수정된 모임 이름", "수정된 모임 설명", "#000000");

        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}", meeting.getId())
                                .cookie(new Cookie(leaderAccessTokenCookieName, leaderAccessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("수정된 모임 이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("수정된 모임 설명"),
                                        fieldWithPath("symbolColor").type(JsonFieldType.STRING).description("수정된 모임 색상")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data.meetingId").type(JsonFieldType.NUMBER).description("수정된 모임 ID"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("수정된 모임 이름"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("수정된 모임 설명"),
                                        fieldWithPath("data.symbolColor").type(JsonFieldType.STRING).description("수정된 모임 색상")
                                )
                        )
                );

        Participant participant = createParticipant(meeting, Role.PARTICIPANT);
        Tokens participantTokens = tokenProvider.issueTokens(participant.getId());
        String participantAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String participantAccessToken = participantTokens.accessToken();

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}", meeting.getId())
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

    @DisplayName("모임 정보를 수정할 때 변경된 사항이 없는 경우 실패한다.")
    @Test
    void modifyMeeting_NO_CHANGES() throws Exception {
        // given
        Meeting meeting = createMeeting();

        Participant leader = createParticipant(meeting, Role.LEADER);
        Tokens leaderTokens = tokenProvider.issueTokens(leader.getId());
        String leaderAccessTokenCookieName = authTokenCookieNameGenerator.generateCookieName(TokenType.ACCESS_TOKEN, meeting.getId());
        String leaderAccessToken = leaderTokens.accessToken();

        ModifyMeetingRequestDto modifyRequestDto = new ModifyMeetingRequestDto(meeting.getName(), meeting.getDescription(), meeting.getSymbolColor());

        // when & then
        mockMvc.perform(
                        patch("/api/v1/meetings/{meetingId}", meeting.getId())
                                .cookie(new Cookie(leaderAccessTokenCookieName, leaderAccessToken))
                                .content(objectMapper.writeValueAsString(modifyRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
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
                .leaderAuthKey("1234")
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
                fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드"),
                fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
        );
    }
}
