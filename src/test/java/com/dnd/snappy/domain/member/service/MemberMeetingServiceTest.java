package com.dnd.snappy.domain.member.service;

import static com.dnd.snappy.domain.member.exception.ParticipantErrorCode.ALREADY_PARTICIPATE_MEETING;
import static com.dnd.snappy.domain.member.exception.ParticipantErrorCode.DUPLICATED_NICKNAME;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.member.entity.MemberMeeting;
import com.dnd.snappy.domain.member.entity.Role;
import com.dnd.snappy.domain.member.repository.MemberMeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberMeetingServiceTest {

    @InjectMocks
    private MemberMeetingService memberMeetingService;

    @Mock
    private MemberMeetingRepository memberMeetingRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @DisplayName("사용자는 모임에 참여할 수 있다.")
    @Test
    void joinMeeting() {
        //given
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.MEMBER;
        Meeting meeting = Meeting.builder().id(meetingId).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).build();
        given(memberMeetingRepository.existsByMemberIdAndMeetingId(memberId, meetingId)).willReturn(false);
        given(memberMeetingRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(false);
        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when
        memberMeetingService.joinMeeting(memberId, meetingId, nickname, role);

        //then
        verify(memberMeetingRepository, times(1)).save(any(MemberMeeting.class));
    }

    @DisplayName("이미 모임에 참여한 사용자라면 예외가 발생한다.")
    @Test
    void already_join_meeting_throw_exception() {
        //given
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.MEMBER;
        given(memberMeetingRepository.existsByMemberIdAndMeetingId(memberId, meetingId)).willReturn(true);

        //when //then
        assertThatThrownBy(() -> memberMeetingService.joinMeeting(memberId, meetingId, nickname, role))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_PARTICIPATE_MEETING.getMessage());
    }

    @DisplayName("해당 모임에 중복된 닉네임이 있다면 예외가 발생한다.")
    @Test
    void duplicate_nickname_in_meeting_throw_exception() {
        //given
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.MEMBER;
        given(memberMeetingRepository.existsByMemberIdAndMeetingId(memberId, meetingId)).willReturn(false);
        given(memberMeetingRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(true);

        //when //then
        assertThatThrownBy(() -> memberMeetingService.joinMeeting(memberId, meetingId, nickname, role))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(DUPLICATED_NICKNAME.getMessage());
    }

    @DisplayName("존재하지 않는 모임에 참여한다면 예외가 발생한다.")
    @Test
    void not_found_meeting_throw_exception() {
        //given
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.MEMBER;
        given(memberMeetingRepository.existsByMemberIdAndMeetingId(memberId, meetingId)).willReturn(false);
        given(memberMeetingRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(false);
        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> memberMeetingService.joinMeeting(memberId, meetingId, nickname, role))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_NOT_FOUND.getMessage());
    }

    @DisplayName("모임이 끝났을때 참여한다면 예외가 발생한다.")
    @Test
    void join_finish_meeting_throw_exception() {
        //given
        Long memberId = 1L;
        Long meetingId = 2L;
        String nickname = "nick";
        Role role = Role.MEMBER;
        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .startDate(LocalDateTime.now().minusDays(3))
                .endDate(LocalDateTime.now().minusNanos(1))
                .build();
        given(memberMeetingRepository.existsByMemberIdAndMeetingId(memberId, meetingId)).willReturn(false);
        given(memberMeetingRepository.existsByNicknameAndMeetingId(nickname, meetingId)).willReturn(false);
        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when //then
        assertThatThrownBy(() -> memberMeetingService.joinMeeting(memberId, meetingId, nickname, role))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MeetingErrorCode.MEETING_JOIN_DENIED.getMessage());
    }
}