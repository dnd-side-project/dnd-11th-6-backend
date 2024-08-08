package com.dnd.snappy.domain.meeting.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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

        BDDMockito.given(meetingRepository.findByMeetingLink(meetingLink)).willReturn(Optional.of(meeting));

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

        BDDMockito.given(meetingRepository.findByMeetingLink(meetingLink)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> meetingService.findByMeetingLink(meetingLink))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_LINK_NOT_FOUND.getMessage());
    }

    @DisplayName("모임을 생성한다.")
    @Test
    void createMeeting() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(1);
        LocalDateTime endDate = startDate.plusHours(1);

        CreateMeetingRequestDto requestDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234",
                "1234"
        );

        when(meetingRepository.existsByMeetingLink(anyString())).thenReturn(false);

        // When
        CreateMeetingResponseDto responseDto = meetingService.createMeeting(requestDto);

        // Then
        assertNotNull(responseDto);
        assertThat(responseDto.meetingLink()).startsWith("https://www.snappy.com/");

        verify(meetingRepository).existsByMeetingLink(anyString());
        verify(meetingRepository).save(any(Meeting.class));
    }

    @DisplayName("시작일이 현재 시간 이전인 경우 예외 발생")
    @Test
    void createMeeting_BAD_REQUEST_startDate() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusHours(1);

        CreateMeetingRequestDto requestDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234",
                "1234"
        );

        // When & Then
        assertThatThrownBy(() -> meetingService.createMeeting(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(CommonErrorCode.BAD_REQUEST.getMessage());

    }

    @DisplayName("시작일이 현재 시간보다 10일 이상 늦은 경우 예외 발생")
    @Test
    void createMeeting_BAD_REQUEST_tenDaysLater() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(11);
        LocalDateTime endDate = startDate.plusHours(1);

        CreateMeetingRequestDto requestDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234",
                "1234"
        );

        // When & Then

        assertThatThrownBy(() -> meetingService.createMeeting(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(CommonErrorCode.BAD_REQUEST.getMessage());
    }

    @DisplayName("종료일이 시작일 이전인 경우 예외 발생")
    @Test
    void createMeeting_BAD_REQUEST_endDate() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(1);
        LocalDateTime endDate = startDate.minusHours(1);

        CreateMeetingRequestDto requestDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234",
                "1234"
        );

        // When & Then
        assertThatThrownBy(() -> meetingService.createMeeting(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(CommonErrorCode.BAD_REQUEST.getMessage());
    }
}