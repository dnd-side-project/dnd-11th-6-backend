package com.dnd.snappy.domain.snap.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.entity.MissionParticipant;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.snap.dto.response.SnapResponseDto;
import com.dnd.snappy.domain.snap.entity.MeetingMissionSnap;
import com.dnd.snappy.domain.snap.entity.RandomMissionSnap;
import com.dnd.snappy.domain.snap.entity.SimpleSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SnapRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RandomMissionRepository randomMissionRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private SnapRepository snapRepository;

    @Autowired
    private RandomMissionSnapRepository randomMissionSnapRepository;

    @DisplayName("커서 기반 페이지네이션을 통해 snaps을 조회할 수 있다.")
    @Test
    void findSnapsInMeetingByCursorId() {
        //given
        Meeting meeting = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Meeting meeting2 = appendMeeting(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        Participant participant = appendParticipant(meeting, "nick", 2);
        List<RandomMission> randomMissions = appendRandomMissions(20);
        List<Mission> missions = appendMissions(meeting, 20);
        long lastId = 0;
        for(int i=0; i<20; i++) {
            appendSimpleSnap(meeting, participant);
            appendRandomMissionSnap(meeting, participant, randomMissions.get(i));
            Snap snap = appendMeetingMissionSnap(meeting, participant, missions.get(i));
            appendSimpleSnap(meeting2, participant);

            lastId = snap.getId();
        }

        //when
        List<SnapResponseDto> snaps = snapRepository.findSnapsInMeetingByCursorId(lastId+1, meeting.getId(), PageRequest.of(0, 20));
        System.out.println("snap = " + snaps.get(0));

        //then
        assertThat(snaps).hasSize(20);
    }

    public List<RandomMission> appendRandomMissions(int count) {
        List<RandomMission> randomMissions = new ArrayList<>();
        for(int i=1; i<=count; i++) {
            RandomMission randomMission = RandomMission.builder()
                    .content("test content " + i)
                    .build();
            randomMissions.add(randomMission);
        }

        return randomMissionRepository.saveAll(randomMissions);
    }

    public List<Mission> appendMissions(Meeting meeting, int count) {
        List<Mission> missions = new ArrayList<>();
        for(int i=1; i<=count; i++) {
            Mission mission = Mission.builder()
                    .meeting(meeting)
                    .content("miss content " + i)
                    .build();
            missions.add(mission);
        }

        return missionRepository.saveAll(missions);
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

    private RandomMission appendRandomMission() {
        RandomMission randomMission = RandomMission.builder().content("test random mission content").build();
        return randomMissionRepository.save(randomMission);
    }

    private Mission appendMission(Meeting meeting) {
        Mission mission = Mission.builder().content("test mission content").meeting(meeting).build();
        return missionRepository.save(mission);
    }

    private Snap appendSimpleSnap(Meeting meeting, Participant participant) {
        SimpleSnap snap = SimpleSnap.builder().meeting(meeting).participant(participant).snapUrl("url1")
                .shootDate(LocalDateTime.now()).build();
        return snapRepository.save(snap);
    }

    private Snap appendRandomMissionSnap(Meeting meeting, Participant participant, RandomMission randomMission) {
        RandomMissionSnap snap = RandomMissionSnap.builder().meeting(meeting).participant(participant).snapUrl("url1").shootDate(LocalDateTime.now()).randomMission(randomMission).build();
        return snapRepository.save(snap);
    }

    private Snap appendMeetingMissionSnap(Meeting meeting, Participant participant, Mission mission) {
        MeetingMissionSnap snap = MeetingMissionSnap.builder().meeting(meeting).participant(participant).snapUrl("url1").shootDate(LocalDateTime.now()).mission(mission).build();
        return snapRepository.save(snap);
    }
}