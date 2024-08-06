package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.DuplicationException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
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
        String meetingLink = generateMeetingLink();
        checkMeetingLinkDuplication(meetingLink);
        MeetingLinkStatus meetingLinkStatus = MeetingLinkStatus.calculateStatus(requestDto.startDate(), requestDto.endDate(), LocalDateTime.now());

        CreateMeetingEntityDto dto = createMeetingEntityDto(requestDto, meetingLink, meetingLinkStatus);
        Meeting meeting = dto.toEntity();

        meeting.validateStartAndEndDates();
        meetingRepository.save(meeting);

        return new CreateMeetingResponseDto(meetingLink, meetingLinkStatus);
    }

    private void checkMeetingLinkDuplication(String meetingLink) {
        if (meetingRepository.existsByMeetingLink(meetingLink)) {
            throw new DuplicationException(CommonErrorCode.DUPLICATION, "모임 링크가 중복되었습니다.");
        }
    }

    private String generateMeetingLink() {
        String uuid = UUID.randomUUID().toString();
        String shortUuid = uuid.replace("-", "").substring(0, 7);
        return "https://www.snappy.com/" + shortUuid;
    }

    private CreateMeetingEntityDto createMeetingEntityDto(CreateMeetingRequestDto requestDto, String meetingLink, MeetingLinkStatus meetingLinkStatus) {
        return new CreateMeetingEntityDto(
                requestDto.name(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                null, // TODO: thumbnailUrl은 나중에 설정
                requestDto.symbolColor(),
                requestDto.password(),
                requestDto.adminPassword(),
                meetingLink,
                meetingLinkStatus
        );
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
    }

}



