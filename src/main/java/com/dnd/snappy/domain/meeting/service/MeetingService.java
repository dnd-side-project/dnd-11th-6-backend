package com.dnd.snappy.domain.meeting.service;

import static com.dnd.snappy.domain.meeting.exception.MeetingErrorCode.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.DuplicationException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private static final String LINK_PREFIX = "https://www.snappy.com/";

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

    @Transactional(readOnly = true)
    public void validateMeetingLeaderAuthKey(Long meetingId, String password, String leaderAuthKey) {
        Meeting meeting = findByMeetingIdOrThrow(meetingId);

        if(!meeting.isLeaderAuthKeyValid(password, leaderAuthKey)) {
            throw new BusinessException(MEETING_INVALIDATE_PASSWORD);
        }
    }

    private Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND, meetingId));
    }

    @Transactional
    public CreateMeetingResponseDto createMeeting(CreateMeetingRequestDto requestDto) {
        String meetingLink = generateMeetingLink();
        checkMeetingLinkDuplication(meetingLink);

        CreateMeetingEntityDto dto = createMeetingEntityDto(requestDto, meetingLink);
        Meeting meeting = Meeting.create(dto);

        meetingRepository.save(meeting);

        return new CreateMeetingResponseDto(meetingLink);
    }

    private void checkMeetingLinkDuplication(String meetingLink) {
        if (meetingRepository.existsByMeetingLink(meetingLink)) {
            throw new DuplicationException(CommonErrorCode.DUPLICATION, "모임 링크가 중복되었습니다.");
        }
    }

    private String generateMeetingLink() {
        String uuid = UUID.randomUUID().toString();
        String shortUuid = uuid.replace("-", "").substring(0, 7);
        return LINK_PREFIX + shortUuid;
    }

    private CreateMeetingEntityDto createMeetingEntityDto(CreateMeetingRequestDto requestDto, String meetingLink) {
        return new CreateMeetingEntityDto(
                requestDto.name(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                null, // TODO: thumbnailUrl은 나중에 설정
                requestDto.symbolColor(),
                requestDto.password(),
                requestDto.leaderAuthKey(),
                meetingLink
        );
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
    }

}

