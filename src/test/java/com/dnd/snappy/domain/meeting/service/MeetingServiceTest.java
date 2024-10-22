package com.dnd.snappy.domain.meeting.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.ShareMeetingLinkResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.entity.MeetingLinkStatus;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;

import org.assertj.core.api.Assertions;
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
    @Test
    void isCorrectMeetingPassword() {
        //given
        Long meetingId = 1L;
        String password = "password";
        Meeting meeting = Meeting.builder().id(meetingId).password(password).build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when //then
        assertDoesNotThrow(() -> meetingService.validateMeetingPassword(meetingId, password));
    }

    @DisplayName("모임 id에 해당하는 모임이 없다면 예외가 발생한다.")
    @Test
    void isCorrectMeetingPassword_notFound() {
        //given
        Long meetingId = 1L;

        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> meetingService.validateMeetingPassword(meetingId, "password"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_NOT_FOUND.getMessage());
    }

    @DisplayName("모임의 비밀번호가 맞지 않다면 예외가 발생한다.")
    @Test
    void isCorrectMeetingPassword_invalidPassword() {
        //given
        Long meetingId = 1L;
        String password = "password";
        Meeting meeting = Meeting.builder().id(meetingId).password(password).build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when //then
        assertThatThrownBy(() -> meetingService.validateMeetingPassword(meetingId, "wrongPassword"))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_INVALIDATE_PASSWORD.getMessage());
    }

    @DisplayName("모임의 관리자 인증키가 맞지 않다면 예외가 발생한다.")
    @Test
    void isCorrectMeetingPassword_invalidLeaderAuthKey() {
        //given
        Long meetingId = 1L;
        String password = "password";
        String leaderAuthKey = "leaderAuthKey";
        Meeting meeting = Meeting.builder().id(meetingId).password(password).leaderAuthKey(leaderAuthKey).build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when //then
        assertThatThrownBy(() -> meetingService.validateMeetingLeaderAuthKey(meetingId, "wrong leaderAuthKey"))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_INVALIDATE_AUTH_KEY.getMessage());

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
                "1234"
        );

        // When
        CreateMeetingResponseDto responseDto = meetingService.createMeeting(requestDto, null);

        // Then
        assertNotNull(responseDto);
        assertThat(responseDto.meetingLink());
        assertThat(responseDto.leaderAuthKey());

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
                "1234"
        );

        // When & Then
        assertThatThrownBy(() -> meetingService.createMeeting(requestDto, null))
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
                "1234"
        );

        // When & Then
        assertThatThrownBy(() -> meetingService.createMeeting(requestDto, null))
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
                "1234"
        );

        // When & Then
        assertThatThrownBy(() -> meetingService.createMeeting(requestDto, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(CommonErrorCode.BAD_REQUEST.getMessage());
    }

    @DisplayName("종료일이 시작일로부터 7일 이내가 아닌 경우 예외 발생")
    @Test
    void createMeeting_BAD_REQUEST_endDate_eightDaysLater() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.plusDays(1);
        LocalDateTime endDate = startDate.plusDays(8);

        CreateMeetingRequestDto requestDto = new CreateMeetingRequestDto(
                "DND",
                "DND 모임 입니다.",
                startDate,
                endDate,
                "#FFF",
                "1234"
        );

        // When & Then
        assertThatThrownBy(() -> meetingService.createMeeting(requestDto, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageStartingWith(CommonErrorCode.BAD_REQUEST.getMessage());
    }

    @DisplayName("모임 ID로 모임 링크를 조회한다.")
    @Test
    void getShareableMeetingLink() {
        // given
        Long meetingId = 1L;
        String meetingLink = "05289e9";
        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .meetingLink(meetingLink)
                .build();

        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        // when
        ShareMeetingLinkResponseDto response = meetingService.getShareableMeetingLink(meetingId);

        // then
        assertThat(response)
                .extracting("meetingLink")
                .isEqualTo(meetingLink);
    }

    @DisplayName("모임 ID에 해당하는 모임이 없다면 예외가 발생한다.")
    @Test
    void findByMeetingIdOrThrow_MEETING_NOT_FOUND() {
        // given
        Long meetingId = 999L;

        given(meetingRepository.findById(meetingId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> meetingService.getShareableMeetingLink(meetingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_NOT_FOUND.getMessage());
    }

    @DisplayName("모임 세부 정보를 조회한다.")
    @Test
    void findMeetingDetailById() {
        //given
        Long meetingId = 1L;
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Meeting meeting = Meeting.builder()
                .id(meetingId)
                .name("DND")
                .description("DND 모임 입니다.")
                .symbolColor("#FFF")
                .thumbnailUrl("thumbnailUrl")
                .startDate(LocalDateTime.now())
                .endDate(endDate)
                .meetingLink("meetingLink")
                .build();
        given(meetingRepository.findById(meetingId)).willReturn(Optional.of(meeting));

        //when
        MeetingDetailResponseDto result = meetingService.findMeetingDetailById(meetingId);

        //then
        Assertions.assertThat(result).isEqualTo(new MeetingDetailResponseDto(
                meeting.getId(),
                meeting.getName(),
                meeting.getDescription(),
                meeting.getThumbnailUrl(),
                meeting.getSymbolColor(),
                meeting.getStartDate(),
                meeting.getEndDate(),
                MeetingLinkStatus.IN_PROGRESS,
                endDate.plusDays(7)
        ));
    }
}
