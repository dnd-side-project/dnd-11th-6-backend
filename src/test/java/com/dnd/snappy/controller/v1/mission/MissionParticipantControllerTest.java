package com.dnd.snappy.controller.v1.mission;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.token.service.TokenProvider;
import com.dnd.snappy.domain.token.service.TokenType;
import com.dnd.snappy.support.RestDocsSupport;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MissionParticipantControllerTest extends RestDocsSupport {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionParticipantRepository missionParticipantRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("참여자가 수행한 미션을 조회한다.")
    @Test
    void findCompletedMissions() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "nick", 5);
        List<Mission> missions = List.of(
                appendMission(meeting, "test mission content1"),
                appendMission(meeting, "test mission content2"),
                appendMission(meeting, "test mission content3")
        );
        List<MissionParticipant> missionParticipants = List.of(
                appendMissionParticipant(missions.get(0), participant),
                appendMissionParticipant(missions.get(2), participant)
        );

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/missions/completed", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN)))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증을 위한 access token")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("참여자가 완료한 모임 미션들"),
                                        fieldWithPath("data[].missionId").type(JsonFieldType.NUMBER).description("완료한 모임 미션 ID"),
                                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("미션 내용")
                                )
                        )
                );
    }

    @DisplayName("참여자가 수행하지 않은 미션을 조회한다.")
    @Test
    void findNotCompletedMissions() throws Exception {
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "nick", 5);
        List<Mission> missions = List.of(
                appendMission(meeting, "test mission content1"),
                appendMission(meeting, "test mission content2"),
                appendMission(meeting, "test mission content3")
        );
        List<MissionParticipant> missionParticipants = List.of(
                appendMissionParticipant(missions.get(0), participant)
        );

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/v1/meetings/{meetingId}/missions/incomplete", meeting.getId())
                                .cookie(new Cookie("ACCESS_TOKEN_" + meeting.getId(), tokenProvider.issueToken(participant.getId(), TokenType.ACCESS_TOKEN)))
                )
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("meetingId").description("모임 ID")
                                ),
                                requestCookies(
                                        cookieWithName("ACCESS_TOKEN_" + meeting.getId()).description("인증을 위한 access token")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("참여자가 완료하지 않은 모임 미션들"),
                                        fieldWithPath("data[].missionId").type(JsonFieldType.NUMBER).description("완료하지 않은 모임 미션 ID"),
                                        fieldWithPath("data[].content").type(JsonFieldType.STRING).description("미션 내용")
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
    private Mission appendMission(Meeting meeting, String content) {
        Mission mission = Mission.builder().content(content).meeting(meeting).build();
        return missionRepository.save(mission);
    }

    private MissionParticipant appendMissionParticipant(Mission mission, Participant participant) {
        MissionParticipant missionParticipant = MissionParticipant.builder().mission(mission).participant(participant).build();
        return missionParticipantRepository.save(missionParticipant);
    }
}