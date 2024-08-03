package com.dnd.snappy.domain.meeting.service;

import static org.assertj.core.api.Assertions.*;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
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

    @Test
    void 모임_링크를_통해_모임을_조회한다() {
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

    @Test
    void 모임_링크에_해당하는_모임이_없는_경우_예외가_발생한다() {
        //given
        String meetingLink = "meetingLink";

        BDDMockito.given(meetingRepository.findByMeetingLink(meetingLink)).willReturn(Optional.empty());

        //when //then
        assertThatThrownBy(() -> meetingService.findByMeetingLink(meetingLink))
                .isInstanceOf(NotFoundException.class)
                .hasMessageStartingWith(MeetingErrorCode.MEETING_LINK_NOT_FOUND.getMessage());
    }

}