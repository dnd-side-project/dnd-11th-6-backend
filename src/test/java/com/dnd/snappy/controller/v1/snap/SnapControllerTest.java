package com.dnd.snappy.controller.v1.snap;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.controller.v1.snap.request.CreateSnapRequest;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SnapControllerTest extends RestDocsSupport {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("Simple Snap을 등록한다.")
    @Test
    void createSimpleSnap() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "jaja", 0);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateSnapRequest createSnapRequest = new CreateSnapRequest(LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/simple", meeting.getId())
                                .file(new MockMultipartFile("snap", "snap.json", MediaType.APPLICATION_JSON_VALUE, request.getBytes(StandardCharsets.UTF_8)))
                                .file(image)
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증을 위한 access token")
                                ),
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestParts(
                                        partWithName("snap").description("JSON 형태의 snap 정보"),
                                        partWithName("image").description("업로드할 이미지 파일")
                                ),
                                requestPartFields("snap",
                                        fieldWithPath("shootDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-ddTHH:mm")).description("사진 촬영 시간")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.snapId").type(JsonFieldType.NUMBER).description("생성된 snap ID"),
                                        fieldWithPath("data.snapUrl").type(JsonFieldType.STRING).description("snap 이미지 url")
                                )
                        )
                );

    }

    @DisplayName("모임이 진행중이 아닐때는 예외가 발생한다.")
    @Test
    void createSimpleSnap_meeting_no_in_progress() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 0);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateSnapRequest createSnapRequest = new CreateSnapRequest(LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/simple", meeting.getId())
                                .file(new MockMultipartFile("snap", "snap.json", MediaType.APPLICATION_JSON_VALUE, request.getBytes(StandardCharsets.UTF_8)))
                                .file(image)
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("공유 가능한 모임 링크"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );

    }

    @DisplayName("참가자의 사진 촬영 횟수가 최대 횟수를 초과한 경우 예외가 발생한다.")
    @Test
    void createSimpleSnap_exceed_max_shoot_count() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 10);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateSnapRequest createSnapRequest = new CreateSnapRequest(LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/simple", meeting.getId())
                                .file(new MockMultipartFile("snap", "snap.json", MediaType.APPLICATION_JSON_VALUE, request.getBytes(StandardCharsets.UTF_8)))
                                .file(image)
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("공유 가능한 모임 링크"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
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

    private Participant appendParticipant(Meeting meeting, String nickname, int shootCount) {
        Participant participant = Participant.builder()
                .nickname(nickname)
                .role(Role.LEADER)
                .shootCount(shootCount)
                .meeting(meeting)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
        return participantRepository.save(participant);
    }


}