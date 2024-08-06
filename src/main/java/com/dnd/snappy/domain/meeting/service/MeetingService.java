package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.entity.MeetingLinkStatus;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    @Transactional(readOnly = true)
    public MeetingDetailResponseDto findByMeetingLink(String meetingLink) {
        Meeting meeting = findByMeetingLinkOrThrow(meetingLink);

        return new MeetingDetailResponseDto(meeting);
    }

    @Transactional
    public CreateMeetingResponseDto createMeeting(CreateMeetingRequestDto requestDto) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime endDate = validateStartAndEndDates(requestDto.startDate(), requestDto.endDate(), now);

        String meetingLink = generateMeetingLink();
        MeetingLinkStatus meetingLinkStatus = MeetingLinkStatus.calculateStatus(requestDto.startDate(), endDate, now);

        Meeting meeting = Meeting.toEntity(requestDto, meetingLink, meetingLinkStatus);
        meetingRepository.save(meeting);

        return new CreateMeetingResponseDto(meetingLink, requestDto.password(), meetingLinkStatus);
    }

    private LocalDateTime validateStartAndEndDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) {
        LocalDateTime tenDaysLater = now.plusDays(10);

        Optional.ofNullable(startDate)
                .filter(date -> !date.isBefore(now) && !date.isAfter(tenDaysLater))
                .orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST, "시작일은 현재 시간 이후부터 10일 이내여야 합니다."));

        if (endDate != null) {
            Optional.of(endDate)
                    .filter(date -> date.isAfter(startDate))
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST, "종료일은 시작일 이후여야 합니다."));
        }

        return endDate;
    }

    private String generateMeetingLink() {
        String uuid = UUID.randomUUID().toString();
        String shortUuid = uuid.replace("-", "").substring(0, 7);
        return "https://www.snappy.com/" + shortUuid;
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
    }

}



