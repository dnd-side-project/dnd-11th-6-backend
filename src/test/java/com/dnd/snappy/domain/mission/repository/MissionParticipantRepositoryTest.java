package com.dnd.snappy.domain.mission.repository;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MissionParticipantRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionParticipantRepository missionParticipantRepository;

    @DisplayName("참여자가 수행하지 않은 미션을 조회할 수 있다.")
    @Test
    void findNotCompletedMissions() {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Meeting meeting2 = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "jae", 1);
        List<Mission> missions = List.of(
                appendMission(meeting, "test mission content1"),
                appendMission(meeting, "test mission content2"),
                appendMission(meeting, "test mission content3"),
                appendMission(meeting2, "test mission content4")
        );
        missionParticipantRepository.save(MissionParticipant.builder().mission(missions.get(0)).participant(participant).build());

        //when
        List<Mission> result = missionParticipantRepository.findNotCompletedMissions(meeting.getId(), participant.getId());

        //then
        Assertions.assertThat(result).hasSize(2)
                .extracting("id", "content")
                .containsExactly(
                        Tuple.tuple(missions.get(1).getId(), missions.get(1).getContent()),
                        Tuple.tuple(missions.get(2).getId(), missions.get(2).getContent())
                );

    }

    @DisplayName("참여자가 수행한 미션을 조회할 수 있다.")
    @Test
    void findCompletedMissions() {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "jae", 1);
        List<Mission> missions = List.of(
                appendMission(meeting, "test mission content1"),
                appendMission(meeting, "test mission content2"),
                appendMission(meeting, "test mission content3")
        );
        missionParticipantRepository.save(MissionParticipant.builder().mission(missions.get(0)).participant(participant).build());
        missionParticipantRepository.save(MissionParticipant.builder().mission(missions.get(2)).participant(participant).build());

        //when
        List<Mission> result = missionParticipantRepository.findCompletedMissions(
                participant.getId());

        //then
        Assertions.assertThat(result).hasSize(2)
                .extracting("id", "content")
                .containsExactly(
                        Tuple.tuple(missions.get(0).getId(), missions.get(0).getContent()),
                        Tuple.tuple(missions.get(2).getId(), missions.get(2).getContent())
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
}