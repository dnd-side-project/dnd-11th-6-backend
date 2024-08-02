package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingDetailResponseDto findByMeetingLink(String meetingLink) {
        Meeting meeting = meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));

        return new MeetingDetailResponseDto(meeting);
    }

}
