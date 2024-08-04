package com.dnd.snappy.domain.meeting.service;

import static com.dnd.snappy.domain.meeting.exception.MeetingErrorCode.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    @Transactional(readOnly = true)
    public MeetingDetailResponseDto findByMeetingLink(String meetingLink) {
        Meeting meeting = findByMeetingLinkOrThrow(meetingLink);

        return new MeetingDetailResponseDto(meeting);
    }

    @Transactional(readOnly = true)
    public void validateMeetingPassword(Long meetingId, String password) {
        Meeting meeting = findByMeetingIdOrThrow(meetingId);

        if(!meeting.isCorrectPassword(password)) {
            throw new BusinessException(MEETING_INVALIDATE_PASSWORD);
        }
    }

    private Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND, meetingId));
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
    }

}
