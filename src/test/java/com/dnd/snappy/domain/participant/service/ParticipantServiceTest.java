package com.dnd.snappy.domain.participant.service;

import static com.dnd.snappy.domain.participant.exception.ParticipantErrorCode.DUPLICATED_NICKNAME;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.participant.dto.response.CreateParticipantResponseDto;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.entity.Role;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @InjectMocks
    private ParticipantService participantService;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @DisplayName("모임에 참여하는 참가자를 생성한다.")
    @Test
    void createParticipant() {
        //given
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.PARTICIPANT;
        Meeting meeting = Meeting.builder().id(meetingId).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).build();
        given(participantRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(false);
        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when
        CreateParticipantResponseDto result = participantService.createParticipant(meetingId, nickname, role);

        //then
        assertThat(result.meetingExpiredDate()).isEqualTo(meeting.getExpiredDate());
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    @DisplayName("해당 모임에 중복된 닉네임이 있다면 예외가 발생한다.")
    @Test
    void duplicate_nickname_in_meeting_throw_exception() {
        //given
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.PARTICIPANT;
        given(participantRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(true);

        //when //then
        assertThatThrownBy(() -> participantService.createParticipant(meetingId, nickname, role))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(DUPLICATED_NICKNAME.getMessage());
    }

    @DisplayName("존재하지 않는 모임에 참여한다면 예외가 발생한다.")
    @Test
    void not_found_meeting_throw_exception() {
        //given
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.PARTICIPANT;
        given(participantRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(false);
        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> participantService.createParticipant(meetingId, nickname, role))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_NOT_FOUND.getMessage());
    }

    @DisplayName("모임이 끝났을때 참여한다면 예외가 발생한다.")
    @Test
    void join_finish_meeting_throw_exception() {
        //given
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.PARTICIPANT;
        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .startDate(LocalDateTime.now().minusDays(3))
                .endDate(LocalDateTime.now().minusNanos(1))
                .build();
        given(participantRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(false);
        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when //then
        assertThatThrownBy(() -> participantService.createParticipant(meetingId, nickname, role))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_JOIN_DENIED.getMessage());
    }
}