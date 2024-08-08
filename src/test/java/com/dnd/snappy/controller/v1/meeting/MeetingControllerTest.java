package com.dnd.snappy.controller.v1.meeting;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.support.RestDocsSupport;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetingControllerTest extends RestDocsSupport {

    @Autowired
    private MeetingRepository meetingRepository;

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
                .adminPassword("adminPassword")
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
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("모임"),
                                        fieldWithPath("data.meetingId").type(JsonFieldType.NUMBER).description("모임 id"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("모임 이름"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("모임 세부 정보"),
                                        fieldWithPath("data.thumbnailUrl").type(JsonFieldType.STRING).description("모임 썸네일 url"),
                                        fieldWithPath("data.symbolColor").type(JsonFieldType.STRING).description("모임 상징 색"),
                                        fieldWithPath("data.startDate").type(JsonFieldType.STRING).attributes(getDateTimeFormat()).description("모임 시작일"),
                                        fieldWithPath("data.endDate").type(JsonFieldType.STRING).attributes(getDateTimeFormat()).description("모임 종료일"),
                                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("모임 링크 상태"),
                                        fieldWithPath("error").type(JsonFieldType.NULL).description("에러")
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
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("모임"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러"),
                                        fieldWithPath("error.status").type(JsonFieldType.NUMBER).description("상태코드"),
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
                "1234",
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
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("생성된 모임 정보"),
                                        fieldWithPath("data.meetingLink").type(JsonFieldType.STRING).description("생성된 모임 링크"),
                                        fieldWithPath("error").type(JsonFieldType.NULL).description("에러")
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
                "1234",
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
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("생성된 모임 정보 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("오류 정보"),
                                        fieldWithPath("error.status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
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
                "팀 회의",
                "이번 주 프로젝트 진행 상황 공유",
                startDate,
                endDate,
                "#FF5733",
                "1234",
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
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("생성된 모임 정보 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("오류 정보"),
                                        fieldWithPath("error.status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
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
                LocalDateTime.now().plusDays(2),
                "",
                "1234",
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
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (실패 시 null)"),
                                        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러 정보"),
                                        fieldWithPath("error.status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드"),
                                        fieldWithPath("error.message").type(JsonFieldType.STRING).optional().description("에러 메시지")
                                )
                        )
                );
    }
}

