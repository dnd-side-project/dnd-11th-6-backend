package com.dnd.snappy.controller.v1.mission;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
import com.dnd.snappy.support.RestDocsSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RandomMissionControllerTest extends RestDocsSupport {

    @Autowired
    private RandomMissionRepository randomMissionRepository;

    @DisplayName("랜덤 미션을 전체 조회한다.")
    @Test
    void findRandomMissions() throws Exception {
        appendRandomMissions();

        mockMvc.perform(
                        get("/api/v1/random-missions")
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("모임"),
                                        fieldWithPath("data[].randomMissionId").type(JsonFieldType.NUMBER).description("랜덤 미션 ID"),
                                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("랜덤 미션 내용")
                                )
                        )
                );
    }

    public List<RandomMission> appendRandomMissions() {
        List<RandomMission> randomMissions = new ArrayList<>();
        for(int i=1; i<=3; i++) {
            RandomMission randomMission = RandomMission.builder()
                    .content("test content " + i)
                    .build();
            randomMissions.add(randomMission);
        }

        return randomMissionRepository.saveAll(randomMissions);
    }
}