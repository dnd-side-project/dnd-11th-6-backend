package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.ModifyMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.ModifyMeetingResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class ModifyMeetingServiceTest {

    @InjectMocks
    private ModifyMeetingService modifyMeetingService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @DisplayName("모임 리더가 모임 정보를 수정한다.")
    @Test
    void modifyMeeting_Success() {
        // given
        Long meetingId = 1L;
        Long participantId = 1L;
        String newName = "수정된 모임 이름";
        String newDescription = "수정된 모임 설명";
        String newSymbolColor = "#ff0000";

        ModifyMeetingRequestDto requestDto = new ModifyMeetingRequestDto(newName, newDescription, newSymbolColor);

        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .name("기존 모임 이름")
                .description("기존 모임 설명")
                .symbolColor("#ffffff")
                .build();

        Participant participant = Participant.builder()
                .id(participantId)
                .nickname("리더")
                .role(Role.LEADER)
                .meeting(meeting)
                .build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));
        given(participantRepository.findById(participantId)).willReturn(Optional.of(participant));

        // when
        ModifyMeetingResponseDto response = modifyMeetingService.modifyMeeting(meetingId, participantId, requestDto);

        // then
        assertThat(response.name()).isEqualTo(newName);
        assertThat(response.description()).isEqualTo(newDescription);
        assertThat(response.symbolColor()).isEqualTo(newSymbolColor);
    }

    @DisplayName("참여하지 않은 모임 ID로 모임을 수정하려 할 때 예외가 발생한다.")
    @Test
    void modifyMeeting_MeetingNotFound() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;
        ModifyMeetingRequestDto requestDto = new ModifyMeetingRequestDto("새 이름", "새 설명", "#ff0000");

        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> modifyMeetingService.modifyMeeting(meetingId, participantId, requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("요청된 모임을 찾을 수 없습니다.");
    }

    @DisplayName("리더가 아닌 참여자가 모임을 수정하려 할 때 예외가 발생한다.")
    @Test
    void modifyMeeting_Unauthorized() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;
        ModifyMeetingRequestDto requestDto = new ModifyMeetingRequestDto("새 이름", "새 설명", "#ff0000");

        Meeting meeting = Meeting.builder().id(meetingId).build();
        Participant participant = Participant.builder()
                .id(participantId)
                .nickname("참여자")
                .role(Role.PARTICIPANT)
                .meeting(meeting)
                .build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));
        given(participantRepository.findById(participantId)).willReturn(Optional.of(participant));

        // when & then
        assertThatThrownBy(() -> modifyMeetingService.modifyMeeting(meetingId, participantId, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("모임 수정 권한이 없습니다.");
    }

    @DisplayName("모임 리더가 아닌 사용자가 수정 시 예외가 발생한다.")
    @Test
    void modifyMeeting_LeaderValidationFailure() {
        // given
        Long meetingId = 1L;
        Long participantId = 2L;
        ModifyMeetingRequestDto requestDto = new ModifyMeetingRequestDto("새 이름", "새 설명", "#ff0000");

        Meeting meeting = Meeting.builder().id(meetingId).build();
        Participant participant = Participant.builder()
                .id(participantId)
                .nickname("참여자")
                .role(Role.PARTICIPANT)
                .meeting(meeting)
                .build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));
        given(participantRepository.findById(participantId)).willReturn(Optional.of(participant));

        // when & then
        assertThatThrownBy(() -> modifyMeetingService.modifyMeeting(meetingId, participantId, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("모임 수정 권한이 없습니다.");
    }
}