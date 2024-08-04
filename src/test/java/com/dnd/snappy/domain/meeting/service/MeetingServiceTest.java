package com.dnd.snappy.domain.meeting.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private MeetingService meetingService;

    @DisplayName("모임 링크를 통해 모임 상세 정보를 조회한다.")
    @Test
    void findByMeetingLink() {
        //given
        String meetingLink = "meetingLink";
        Meeting meeting = Meeting.builder()
                .id(1L)
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink(meetingLink)
                .build();

        given(meetingRepository.findByMeetingLink(meetingLink)).willReturn(Optional.of(meeting));

        //when
        MeetingDetailResponseDto meetingResponse = meetingService.findByMeetingLink(meetingLink);

        //then
        assertThat(meetingResponse)
                .extracting("meetingId", "name", "description", "thumbnailUrl", "symbolColor", "startDate", "endDate")
                .containsExactly(meeting.getId(), meeting.getName(), meeting.getDescription(), meeting.getThumbnailUrl(), meeting.getSymbolColor(), meeting.getStartDate(), meeting.getEndDate());
    }

    @DisplayName("모임 링크에 해당하는 모임이 없다면 예외가 발생한다.")
    @Test
    void findByMeetingLink_notFound() {
        //given
        String meetingLink = "meetingLink";

        given(meetingRepository.findByMeetingLink(meetingLink)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> meetingService.findByMeetingLink(meetingLink))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_LINK_NOT_FOUND.getMessage());
    }

    @DisplayName("모임의 비밀번호가 맞는지 확인 가능하다.")
    @ParameterizedTest
    @CsvSource({
            "password, password, true",
            "password, wrongPassword, false"
    })
    void isCorrectMeetingPassword(String password, String inputPassword, boolean expected) {
        //given
        Long meetingId = 1L;
        Meeting meeting = Meeting.builder().id(meetingId).password(password).build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when
        boolean result = meetingService.isCorrectMeetingPassword(meetingId, inputPassword);

        //thenR
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("모임 id에 해당하는 모임이 없다면 예외가 발생한다.")
    @Test
    void isCorrectMeetingPassword_notFound() {
        //given
        Long meetingId = 1L;

        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> meetingService.isCorrectMeetingPassword(meetingId, "password"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_NOT_FOUND.getMessage());
    }

}