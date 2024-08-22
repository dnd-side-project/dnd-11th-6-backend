package com.dnd.snappy.domain.mission.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.repository.MissionParticipantRepository;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MissionValidationServiceTest {

    @InjectMocks
    private MissionValidationService missionValidationService;

    @Mock
    private MissionParticipantRepository missionParticipantRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private MissionRepository missionRepository;

    @DisplayName("리더가 맞는 경우 validateIsLeader는 성공한다.")
    @Test
    void validateIsLeader_Success() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;

        Participant participant = Participant.builder()
                .id(participantId)
                .role(Role.LEADER)
                .meeting(Meeting.builder().id(meetingId).build())
                .build();

        given(participantRepository.findById(participantId)).willReturn(Optional.of(participant));

        // when & then
        assertDoesNotThrow(() -> missionValidationService.validateIsLeader(participantId, meetingId));
    }

    @DisplayName("리더가 아닌 경우 예외를 던진다.")
    @Test
    void validateIsLeader_exception() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;

        Participant participant = Participant.builder()
                .id(participantId)
                .role(Role.PARTICIPANT)
                .meeting(Meeting.builder().id(meetingId).build())
                .build();

        given(participantRepository.findById(participantId)).willReturn(Optional.of(participant));

        // when & then
        assertThatThrownBy(() -> missionValidationService.validateIsLeader(participantId, meetingId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("모임 미션 생성/수정/삭제 권한이 없습니다.");
    }

    @DisplayName("모집에 참여하는 참여자Id가 존재하지 않을 경우 예외를 던진다.")
    @Test
    void validateIsLeader_ParticipantNotFound() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;

        given(participantRepository.findById(participantId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionValidationService.validateIsLeader(participantId, meetingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("모임에 참여중인 참가자가 아닙니다.");
    }

    @DisplayName("미션에 참여자가 있을 경우 미션을 수정하려 할 시, 예외를 던진다.")
    @Test
    void validateModification_MissionHasParticipants() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        Long missionId = 1L;
        String newContent = "새 내용";

        Mission mission = Mission.builder()
                .id(missionId)
                .content("기존 내용")
                .meeting(Meeting.builder().id(meetingId).build())
                .build();

        given(missionParticipantRepository.existsByMissionId(missionId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> missionValidationService.validateModification(mission, newContent, participantId, meetingId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("미션에 참여자가 있어서 수정/삭제할 수 없습니다.");
    }

    @DisplayName("미션 내용이 변경되지 않은 경우 예외를 던진다.")
    @Test
    void validateModification_Unchanged() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        Long missionId = 1L;
        String newContent = "기존 내용";

        Mission mission = Mission.builder()
                .id(missionId)
                .content("기존 내용")
                .meeting(Meeting.builder().id(meetingId).build())
                .build();

        lenient().when(missionParticipantRepository.existsByMissionId(missionId)).thenReturn(false);
        lenient().when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        lenient().when(participantRepository.findById(participantId)).thenReturn(Optional.of(Participant.builder()
                .id(participantId)
                .role(Role.LEADER)
                .meeting(Meeting.builder().id(meetingId).build())
                .build()));

        // when & then
        assertThatThrownBy(() -> missionValidationService.validateModification(mission, newContent, participantId, meetingId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("변경된 사항이 없습니다.");
    }

    @DisplayName("미션에 참여자가 있을 경우 예외를 던진다.")
    @Test
    void validateMissionForDeletion_MissionHasParticipants() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        Long missionId = 1L;

        given(missionParticipantRepository.existsByMissionId(missionId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> missionValidationService.validateMissionForDeletion(missionId, participantId, meetingId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("미션에 참여자가 있어서 수정/삭제할 수 없습니다.");
    }

    @DisplayName("존재하지 않는 미션 ID로 시도할 시 예외를 던진다.")
    @Test
    void validateMissionForDeletion_MissionNotFound() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        Long missionId = 1L;

        given(missionParticipantRepository.existsByMissionId(missionId)).willReturn(false);
        given(missionRepository.findById(missionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionValidationService.validateMissionForDeletion(missionId, participantId, meetingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("미션이 존재하지 않습니다.");
    }
}