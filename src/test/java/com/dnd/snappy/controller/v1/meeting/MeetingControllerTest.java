package com.dnd.snappy.controller.v1.meeting;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.controller.v1.meeting.request.LeaderAuthKeyValidationRequest;
import com.dnd.snappy.controller.v1.meeting.request.PasswordValidationRequest;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;

import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetingControllerTest extends RestDocsSupport {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("모임 링크를 통해 모임 상세 정보를 조회한다.")
    @Test
    void findByMeetingLink() throws Exception {
        String meetingLink = "xzjdclas";
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink(meetingLink)
                .password("password")
                .leaderAuthKey("leaderAuthKey")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        meetingRepository.save(meeting);

        mockMvc.perform(
                        get("/api/v1/meetings?meetingLink=" + meetingLink)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("meetingLink").description("모임 링크")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("모임"),
                                        fieldWithPath("data.meetingId").type(JsonFieldType.NUMBER).description("모임 id"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("모임 이름"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("모임 세부 정보"),
                                        fieldWithPath("data.thumbnailUrl").type(JsonFieldType.STRING).description("모임 썸네일 url"),
                                        fieldWithPath("data.symbolColor").type(JsonFieldType.STRING).description("모임 상징 색"),
                                        fieldWithPath("data.startDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-dd'T'HH")).description("모임 시작일"),
                                        fieldWithPath("data.endDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-dd'T'HH")).description("모임 종료일"),
                                        fieldWithPath("data.linkStatus").type(JsonFieldType.STRING).attributes(key("format").value("PENDING(모임 시작전) | IN_PROGRESS(진행중) | COMPLETED(모임 종료)")).description("모임 상태"),
                                        fieldWithPath("data.linkExpiredDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-dd'T'HH")).description("모임이 언제까지 유효한지")
                                )
                        )
                );
    }

    @DisplayName("모임 링크에 해당하는 모임이 없다면 예외가 발생한다.")
    @Test
    void findByMeetingLink_notFound() throws Exception {
        String meetingLink = "xzjdclas";
        mockMvc.perform(
                        get("/api/v1/meetings?meetingLink=" + meetingLink)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("meetingLink").description("모임 링크")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("모임"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @DisplayName("모임 링크가 만료된 모임은 예외가 발생한다.")
    @Test
    void findByMeetingLink_MEETING_LINK_EXPIRED() throws Exception {
        String meetingLink = "expiredLink";

        // 만료된 Meeting 객체 생성
        Meeting expiredMeeting = Meeting.builder()
                .name("Expired Meeting")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now().minusDays(15))
                .endDate(LocalDateTime.now().minusDays(10))
                .meetingLink(meetingLink)
                .password("password")
                .leaderAuthKey("leaderAuthKey")
                .createdAt(LocalDateTime.now().minusDays(12))
                .updatedAt(LocalDateTime.now().minusDays(12))
                .build();

        meetingRepository.save(expiredMeeting);

        mockMvc.perform(
                        get("/api/v1/meetings?meetingLink=" + meetingLink)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("meetingLink").description("모임 링크")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("모임"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @DisplayName("모임의 비밀번호가 맞는지 확인한다.")
    @Test
    void validateMeetingPassword() throws Exception {
        //given
        String password = "password";
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink("meetingLink")
                .password(password)
                .leaderAuthKey("leaderAuthKey")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        meeting = meetingRepository.save(meeting);

        PasswordValidationRequest passwordValidationRequest = new PasswordValidationRequest(password);

        //when //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/validate-password", meeting.getId())
                                .content(objectMapper.writeValueAsString(passwordValidationRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("모임 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
                                )
                        )
                );
    }

    @DisplayName("모임의 비밀번호가 유효하지 않다면 예외가 발생한다.")
    @Test
    void validateMeetingPassword_invalidatePassword() throws Exception {
        //given
        String password = "password";
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink("meetingLink")
                .password(password)
                .leaderAuthKey("leaderAuthKey")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        meeting = meetingRepository.save(meeting);

        PasswordValidationRequest passwordValidationRequest = new PasswordValidationRequest("wrongPassword");

        //when //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/validate-password", meeting.getId())
                                .content(objectMapper.writeValueAsString(passwordValidationRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("모임 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @DisplayName("모임의 관리자 인증키가 맞는지 확인한다.")
    @Test
    void validateMeetingLeaderAuthKey() throws Exception {
        //given
        String password = "password";
        String leaderAuthKey = "leaderAuthKey";
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink("meetingLink")
                .password(password)
                .leaderAuthKey(leaderAuthKey)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        meeting = meetingRepository.save(meeting);

        LeaderAuthKeyValidationRequest request = new LeaderAuthKeyValidationRequest(leaderAuthKey);

        //when //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/validate-leader-key", meeting.getId())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("leaderAuthKey").type(JsonFieldType.STRING).description("관리자 인증키")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터")
                                )
                        )
                );
    }

    @DisplayName("모임의 관리자 인증키가 유효하지 않다면 예외가 발생한다.")
    @Test
    void validateMeetingLeaderAuthKey_invalidateLeaderAuthKey() throws Exception {
        //given
        String password = "password";
        String leaderAuthKey = "leaderAuthKey";
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink("meetingLink")
                .password(password)
                .leaderAuthKey(leaderAuthKey)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        meeting = meetingRepository.save(meeting);

        LeaderAuthKeyValidationRequest request = new LeaderAuthKeyValidationRequest("wrong leaderAuthKey");

        //when //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/meetings/{meetingId}/validate-leader-key", meeting.getId())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestFields(
                                        fieldWithPath("leaderAuthKey").type(JsonFieldType.STRING).description("관리자 인증키")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("데이터"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @DisplayName("새로운 모임을 생성한다.")
    @Test
    void createMeeting() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(2);
        LocalDateTime endDate = startDate.plusDays(1);

        CreateMeetingRequestDto requestDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234"
        );

        String meetingJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/meetings")
                        .file(new MockMultipartFile("meeting", "meeting.json", MediaType.APPLICATION_JSON_VALUE, meetingJson.getBytes(StandardCharsets.UTF_8)))
                        .file("thumbnail", new byte[0])
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("meeting").description("모임 정보 JSON"),
                                        partWithName("thumbnail").optional().description("모임 썸네일 (선택)")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("모임"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("모임 이름"),
                                        fieldWithPath("data.meetingLink").type(JsonFieldType.STRING).description("모임 링크"),
                                        fieldWithPath("data.leaderAuthKey").type(JsonFieldType.STRING).description("모임 관리자 인증 키"),
                                        fieldWithPath("data.password").type(JsonFieldType.STRING).description("모임 비밀번호"),
                                        fieldWithPath("data.startDate").type(JsonFieldType.STRING).description("모임 시작일"),
                                        fieldWithPath("data.endDate").type(JsonFieldType.STRING).description("모임 종료일")
                                )
                        )
                );
    }

    @DisplayName("모임 생성 시 시작일은 오늘부터 10일 이내여야 한다.")
    @Test
    void createMeeting_BAD_REQUEST_startDate() throws Exception {
        // 현재 날짜 기준으로 11일 후 시작일, 12일 후 종료일 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(11);
        LocalDateTime endDate = startDate.plusDays(1);

        CreateMeetingRequestDto invalidStartDateDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234"
        );

        String meetingJson = objectMapper.writeValueAsString(invalidStartDateDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/meetings")
                        .file(new MockMultipartFile("meeting", "meeting.json", MediaType.APPLICATION_JSON_VALUE, meetingJson.getBytes(StandardCharsets.UTF_8)))
                        .file("thumbnail", new byte[0])
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("meeting").description("모임 정보 JSON"),
                                        partWithName("thumbnail").optional().description("모임 썸네일 (선택)")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("생성된 모임 정보 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("오류 정보"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).optional().description("에러 메시지")
                                )
                        )
                );
    }

    @DisplayName("모임 생성 시 종료일이 시작일 이전이면 안된다.")
    @Test
    void createMeeting_BAD_REQUEST_endDate() throws Exception {
        // 현재 날짜 기준으로 5일 후 시작일, 시작일 1일 전을 종료일로 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(5);
        LocalDateTime endDate = startDate.minusDays(1);

        CreateMeetingRequestDto invalidEndDateDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234"
        );

        String meetingJson = objectMapper.writeValueAsString(invalidEndDateDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/meetings")
                        .file(new MockMultipartFile("meeting", "meeting.json", MediaType.APPLICATION_JSON_VALUE, meetingJson.getBytes(StandardCharsets.UTF_8)))
                        .file("thumbnail", new byte[0])
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("meeting").description("모임 정보 JSON"),
                                        partWithName("thumbnail").optional().description("모임 썸네일 (선택)")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("생성된 모임 정보 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("오류 정보"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).optional().description("에러 메시지")
                                )
                        )
                );
    }

    @DisplayName("모임 생성 시 종료일이 시작일로부터 7일 이내여야 한다.")
    @Test
    void createMeeting_BAD_REQUEST_endDate_eightDaysLater() throws Exception {
        // 현재 날짜 기준으로 5일 후 시작일, 시작일로부터 8일 후를 종료일로 설정
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(5);
        LocalDateTime endDate = startDate.plusDays(8);

        CreateMeetingRequestDto invalidEndDateDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234"
        );

        String meetingJson = objectMapper.writeValueAsString(invalidEndDateDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/meetings")
                        .file(new MockMultipartFile("meeting", "meeting.json", MediaType.APPLICATION_JSON_VALUE, meetingJson.getBytes(StandardCharsets.UTF_8)))
                        .file("thumbnail", new byte[0])
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("meeting").description("모임 정보 JSON"),
                                        partWithName("thumbnail").optional().description("모임 썸네일 (선택)")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("생성된 모임 정보 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("오류 정보"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).optional().description("에러 메시지")
                                )
                        )
                );
    }

    @DisplayName("모임 생성 시 필수 입력값을 입력하지 않으면 예외 메시지를 던진다.")
    @Test
    void createMeeting_VALIDATION_ERROR() throws Exception {
        // Given: 유효하지 않은 요청 DTO (필수 입력값이 비어있음)
        CreateMeetingRequestDto invalidRequest = new CreateMeetingRequestDto(
                "",
                "",
                LocalDateTime.now().plusDays(1),
                null,
                "",
                "1234"
        );

        String meetingJson = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/meetings")
                        .file(new MockMultipartFile("meeting", "meeting.json", MediaType.APPLICATION_JSON_VALUE, meetingJson.getBytes(StandardCharsets.UTF_8)))
                        .file("thumbnail", new byte[0])
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("meeting").description("모임 정보 JSON"),
                                        partWithName("thumbnail").optional().description("모임 썸네일 (선택)")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러 정보"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).optional().description("에러 메시지")
                                )
                        )
                );
    }

    @DisplayName("모임 ID로 모임 링크를 조회한다.")
    @Test
    void getShareableMeetingLink_Success() throws Exception {
        // Given
        String meetingLink = "05289e9";
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink(meetingLink)
                .password("password")
                .leaderAuthKey("leaderAuthKey")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        meeting = meetingRepository.save(meeting);

        // When & Then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/share", meeting.getId())
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(parameterWithName("meetingId").description("모임 ID")),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data.meetingLink").type(JsonFieldType.STRING).description("모임 링크")
                                )
                        )
                );
    }

    @DisplayName("모임 ID에 해당하는 모임이 없다면 예외가 발생한다.")
    @Test
    void findByMeetingIdOrThrow_MEETING_NOT_FOUND() throws Exception {
        // Given
        Long meetingId = 999L;

        // When & Then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/share", meetingId)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                gerErrorResponse()
                        )
                );
    }

    @DisplayName("meetingId를 통해 모임 세부사항을 조회한다.")
    @Test
    void findMeetingDetailById() throws Exception {
        //given
        Meeting meeting = Meeting.builder()
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(3))
                .meetingLink("sgawra")
                .password("password")
                .leaderAuthKey("leaderAuthKey")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        meetingRepository.save(meeting);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}", meeting.getId())
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("모임"),
                                        fieldWithPath("data.meetingId").type(JsonFieldType.NUMBER).description("모임 id"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("모임 이름"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("모임 세부 정보"),
                                        fieldWithPath("data.thumbnailUrl").type(JsonFieldType.STRING).description("모임 썸네일 url"),
                                        fieldWithPath("data.symbolColor").type(JsonFieldType.STRING).description("모임 상징 색"),
                                        fieldWithPath("data.startDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-dd'T'HH")).description("모임 시작일"),
                                        fieldWithPath("data.endDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-dd'T'HH")).description("모임 종료일"),
                                        fieldWithPath("data.linkStatus").type(JsonFieldType.STRING).attributes(key("format").value("PENDING(모임 시작전) | IN_PROGRESS(진행중) | COMPLETED(모임 종료)")).description("모임 상태"),
                                        fieldWithPath("data.linkExpiredDate").type(JsonFieldType.STRING).attributes(key("format").value("yyyy-MM-dd'T'HH")).description("모임이 언제까지 유효한지")
                                )
                        )
                );

    }

    @DisplayName("모임 id에 해당하는 모임이 없을 경우 예외가 발생한다.")
    @Test
    void findMeetingDetailById_not_fond() throws Exception {

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}", 1L)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        restDocs.document(
                                gerErrorResponse()
                        )
                );

    }

    @DisplayName("모임의 비밀번호를 알 수 있다.")
    @Test
    void findMeetingPassword() throws Exception {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "nick", Role.LEADER, 10);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/password", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증을 위한 access token")
                                ),
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("모임"),
                                        fieldWithPath("data.password").type(JsonFieldType.STRING).description("모임 비밀번호"),
                                        fieldWithPath("data.leaderAuthKey").type(JsonFieldType.STRING).description("모임 관리자키")
                                )
                        )
                );

    }

    @DisplayName("모임의 비밀번호를 알 수 있다.")
    @Test
    void findMeetingPassword_no_leader() throws Exception {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "nick", Role.PARTICIPANT, 10);
        String token = tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/password", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), token))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증을 위한 access token")
                                ),
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("모임"),
                                        fieldWithPath("data.password").type(JsonFieldType.STRING).description("모임 비밀번호")
                                )
                        )
                );

    }

    private ResponseFieldsSnippet gerErrorResponse() {
        return responseFields(
                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                fieldWithPath("data").type(JsonFieldType.NULL).description("공유 가능한 모임 링크"),
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
                .password("1234")
                .leaderAuthKey("1423")
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
