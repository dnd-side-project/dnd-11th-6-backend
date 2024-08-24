package com.dnd.snappy.domain.mission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.dto.response.LeaderMeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.dto.response.MeetingMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionRepository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MissionDetailServiceTest {

    @InjectMocks
    private MissionDetailService missionDetailService;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MissionValidationService missionValidationService;

    @DisplayName("미션 목록을 조회한다.")
    @Test
    void findMeetingMissions() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;

        List<Mission> missions = List.of(
                Mission.builder().id(1L).content("미션 내용 1").build(),
                Mission.builder().id(2L).content("미션 내용 2").build(),
                Mission.builder().id(3L).content("미션 내용 3").build()
        );

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(Meeting.builder().id(meetingId).build()));
        given(missionRepository.findAllByMeetingId(meetingId)).willReturn(missions);

        // when
        List<MeetingMissionDetailResponseDto> result = missionDetailService.findMeetingMissions(meetingId, participantId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("missionId", "content")
                .containsExactly(
                        Tuple.tuple(1L, "미션 내용 1"),
                        Tuple.tuple(2L, "미션 내용 2"),
                        Tuple.tuple(3L, "미션 내용 3")
                );
    }

    @DisplayName("리더가 미션 목록을 조회한다.")
    @Test
    void findLeaderMeetingMissions() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;

        Meeting meeting = Meeting.builder().id(meetingId).build();
        List<Mission> missions = List.of(
                Mission.builder().id(1L).content("미션 내용 1").build(),
                Mission.builder().id(2L).content("미션 내용 2").build(),
                Mission.builder().id(3L).content("미션 내용 3").build()
        );

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));
        doNothing().when(missionValidationService).validateIsLeader(participantId, meetingId);

        given(missionRepository.findLeaderMeetingMissions(meetingId)).willReturn(
                List.of(
                        new LeaderMeetingMissionDetailResponseDto(1L, "미션 내용 1", false),
                        new LeaderMeetingMissionDetailResponseDto(2L, "미션 내용 2", false),
                        new LeaderMeetingMissionDetailResponseDto(3L, "미션 내용 3", false)
                )
        );

        // when
        List<LeaderMeetingMissionDetailResponseDto> result = missionDetailService.findLeaderMeetingMissions(meetingId, participantId);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("missionId", "content", "hasParticipants")
                .containsExactly(
                        Tuple.tuple(1L, "미션 내용 1", false),
                        Tuple.tuple(2L, "미션 내용 2", false),
                        Tuple.tuple(3L, "미션 내용 3", false)
                );
    }

    @DisplayName("참여하지 않은 모임 ID로 미션 목록을 조회할 때 예외가 발생한다.")
    @Test
    void findMeetingMissions_MeetingNotFound() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;

        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionDetailService.findMeetingMissions(meetingId, participantId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("요청된 모임을 찾을 수 없습니다.");
    }

    @DisplayName("리더가 아닌 사용자가 리더 전용 미션 목록을 조회할 때 예외가 발생한다.")
    @Test
    void findLeaderMeetingMissions_UNAUTHORIZED_MISSION() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;

        Meeting meeting = Meeting.builder().id(meetingId).build();
        List<Mission> missions = List.of(
                Mission.builder().id(1L).content("미션 내용 1").build(),
                Mission.builder().id(2L).content("미션 내용 2").build(),
                Mission.builder().id(3L).content("미션 내용 3").build()
        );

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        doThrow(new BusinessException(MissionErrorCode.UNAUTHORIZED_MISSION))
                .when(missionValidationService).validateIsLeader(participantId, meetingId);

        // when & then
        assertThatThrownBy(() -> missionDetailService.findLeaderMeetingMissions(meetingId, participantId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("모임 미션 생성/수정/삭제 권한이 없습니다.");
    }

}
