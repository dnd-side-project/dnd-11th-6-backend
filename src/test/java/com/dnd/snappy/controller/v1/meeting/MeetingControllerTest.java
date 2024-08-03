package com.dnd.snappy.controller.v1.meeting;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.support.RestDocsSupport;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
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
}