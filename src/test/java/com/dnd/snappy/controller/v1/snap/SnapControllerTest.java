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

import com.dnd.snappy.controller.v1.snap.request.CreateMeetingMissionSnapRequest;
import com.dnd.snappy.controller.v1.snap.request.CreateRandomMissionSnapRequest;
import com.dnd.snappy.controller.v1.snap.request.CreateSnapRequest;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
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
    private RandomMissionRepository randomMissionRepository;

    @Autowired
    private MissionRepository missionRepository;

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

    @DisplayName("랜덤 미션을 수행한 Snap 을 등록한다.")
    @Test
    void createRandomMissionSnap() throws Exception {
        RandomMission randomMission = appendRandomMission();
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 0);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateRandomMissionSnapRequest createSnapRequest = new CreateRandomMissionSnapRequest(randomMission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/random-mission", meeting.getId())
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
                                        fieldWithPath("randomMissionId").type(JsonFieldType.NUMBER).description("수행한 랜덤 미션 ID"),
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

    @DisplayName("랜덤 미션을 수행한 사진을 등록할때 모임이 진행중이 아니라면 예외가 발생한다.")
    @Test
    void createRandomMissionSnap_meeting_not_in_progress() throws Exception {
        RandomMission randomMission = appendRandomMission();
        Meeting meeting = appendMeeting(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 0);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateRandomMissionSnapRequest createSnapRequest = new CreateRandomMissionSnapRequest(randomMission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/random-mission", meeting.getId())
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

    @DisplayName("랜덤 미션을 수행한 사진을 등록할때 참여자의 촬영 횟수가 최대치를 넘었으면 예외가 발생한다.")
    @Test
    void createRandomMissionSnap_exceed_max_shoot_count() throws Exception {
        RandomMission randomMission = appendRandomMission();
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 10);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateRandomMissionSnapRequest createSnapRequest = new CreateRandomMissionSnapRequest(randomMission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/random-mission", meeting.getId())
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

    @DisplayName("모임 미션을 수행한 Snap 을 등록한다.")
    @Test
    void createMeetingMissionSnap() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 0);
        Mission mission = appendMission(meeting);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateMeetingMissionSnapRequest createSnapRequest = new CreateMeetingMissionSnapRequest(mission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/meeting-mission", meeting.getId())
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
                                        fieldWithPath("missionId").type(JsonFieldType.NUMBER).description("수행한 모임 미션 ID"),
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

    @DisplayName("모임 미션을 수행한 사진을 등록할때 모임이 진행중이 아니라면 예외가 발생한다.")
    @Test
    void createMeetingMissionSnap_meeting_not_in_progress() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 0);
        Mission mission = appendMission(meeting);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateMeetingMissionSnapRequest createSnapRequest = new CreateMeetingMissionSnapRequest(mission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/meeting-mission", meeting.getId())
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

    @DisplayName("모임 미션을 수행한 사진을 등록할때 참여자의 촬영 횟수가 최대치를 넘었으면 예외가 발생한다.")
    @Test
    void createMeetingMissionSnap_exceed_max_shoot_count() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 10);
        Mission mission = appendMission(meeting);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateMeetingMissionSnapRequest createSnapRequest = new CreateMeetingMissionSnapRequest(mission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/meeting-mission", meeting.getId())
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

    @DisplayName("모임 미션을 수행한 사진을 등록할때 참가자가 참여한 모임의 미션이 아닌경우 예외 발생")
    @Test
    void createMeetingMissionSnap_not_participation_mission() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Participant participant = appendParticipant(meeting, "jaja", 10);
        Meeting meeting2 = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        Mission mission = appendMission(meeting2);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);
        CreateMeetingMissionSnapRequest createSnapRequest = new CreateMeetingMissionSnapRequest(mission.getId(), LocalDateTime.now());
        String request = objectMapper.writeValueAsString(createSnapRequest);
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpg", new byte[]{});
        BDDMockito.given(imageUploader.upload(image)).willReturn("snapUrl.jpg");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/meetings/{meetingId}/snaps/meeting-mission", meeting.getId())
                                .file(new MockMultipartFile("snap", "snap.json", MediaType.APPLICATION_JSON_VALUE, request.getBytes(StandardCharsets.UTF_8)))
                                .file(image)
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
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

    private RandomMission appendRandomMission() {
        RandomMission randomMission = RandomMission.builder().content("test random mission content").build();
        return randomMissionRepository.save(randomMission);
    }

    private Mission appendMission(Meeting meeting) {
        Mission mission = Mission.builder().content("test mission content").meeting(meeting).build();
        return missionRepository.save(mission);
    }


}