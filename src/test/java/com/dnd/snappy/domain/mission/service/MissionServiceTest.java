package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.mission.dto.request.CreateMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.request.ModifyMissionRequestDto;
import com.dnd.snappy.domain.mission.dto.response.CreateMissionResponseDto;
import com.dnd.snappy.domain.mission.dto.response.ModifyMissionResponseDto;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @InjectMocks
    private MissionService missionService;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MissionValidationService missionValidationService;

    @DisplayName("모임 리더가 미션을 생성한다.")
    @Test
    void createMission() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        String missionContent = "미션 내용";
        CreateMissionRequestDto requestDto = new CreateMissionRequestDto(missionContent);

        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));
        given(missionRepository.save(any(Mission.class))).willReturn(Mission.builder().content(missionContent).build());

        // when
        CreateMissionResponseDto response = missionService.createMission(meetingId, participantId, requestDto);

        // then
        assertThat(response.content()).isEqualTo(missionContent);
        verify(missionValidationService, times(1)).validateIsLeader(participantId, meetingId);
        verify(missionRepository, times(1)).save(any(Mission.class));
    }

    @DisplayName("존재하지 않는 모임 ID로 미션을 생성하려고 할 때 실패한다.")
    @Test
    void createMission_InvalidMeetingId() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        String missionContent = "미션 내용";
        CreateMissionRequestDto requestDto = new CreateMissionRequestDto(missionContent);

        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.createMission(meetingId, participantId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("요청된 모임을 찾을 수 없습니다.[id: " + meetingId + " is not found]");

        verify(missionRepository, times(0)).save(any(Mission.class));
    }

    @DisplayName("모임 리더가 미션을 수정한다.")
    @Test
    void modifyMission() {
        // given
        Long meetingId = 1L;
        Long missionId = 1L;
        Long participantId = 1L;
        String newMissionContent = "수정된 미션 내용";
        ModifyMissionRequestDto requestDto = new ModifyMissionRequestDto(newMissionContent);

        Mission existingMission = Mission.builder()
                .id(missionId)
                .content("기존 미션 내용")
                .build();
        Meeting meeting = Meeting.builder().id(meetingId).build();

        // 필요한 스텁 설정
        lenient().when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        lenient().when(missionRepository.findById(missionId)).thenReturn(Optional.of(existingMission));

        // when
        ModifyMissionResponseDto response = missionService.modifyMission(meetingId, missionId, participantId, requestDto);

        // then
        assertThat(response.content()).isEqualTo(newMissionContent);
        verify(missionValidationService, times(1)).validateIsLeader(participantId, meetingId);
        verify(missionValidationService, times(1)).validateModification(existingMission, newMissionContent, participantId, meetingId);
        verify(missionRepository, times(1)).save(argThat(m -> m.getContent().equals(newMissionContent)));
    }

    @DisplayName("존재하지 않는 미션 ID로 미션을 수정하려고 할 때 실패한다.")
    @Test
    void modifyMission_InvalidMissionId() {
        // given
        Long meetingId = 1L;
        Long missionId = 1L;
        Long participantId = 1L;
        ModifyMissionRequestDto requestDto = new ModifyMissionRequestDto("수정된 미션 내용");

        given(missionRepository.findById(missionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.modifyMission(meetingId, missionId, participantId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("미션이 존재하지 않습니다.[id: " + missionId + " is not found]");

        verify(missionRepository, times(0)).save(any(Mission.class));
    }

    @DisplayName("모임 리더가 미션을 삭제한다.")
    @Test
    void deleteMission() {
        // given
        Long meetingId = 1L;
        Long missionId = 1L;
        Long participantId = 1L;

        Mission existingMission = Mission.builder().id(missionId).build();

        given(missionRepository.findById(missionId)).willReturn(Optional.of(existingMission));

        // when
        missionService.deleteMission(meetingId, missionId, participantId);

        // then
        verify(missionValidationService, times(1)).validateMissionForDeletion(missionId, participantId, meetingId);
        verify(missionRepository, times(1)).delete(existingMission);
    }

    @DisplayName("존재하지 않는 미션 ID로 미션을 삭제하려고 할 때 실패한다.")
    @Test
    void deleteMission_InvalidMissionId() {
        // given
        Long meetingId = 1L;
        Long missionId = 1L;
        Long participantId = 1L;

        given(missionRepository.findById(missionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.deleteMission(meetingId, missionId, participantId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("미션이 존재하지 않습니다.[id: " + missionId + " is not found]");

        verify(missionRepository, times(0)).delete(any(Mission.class));
    }
}