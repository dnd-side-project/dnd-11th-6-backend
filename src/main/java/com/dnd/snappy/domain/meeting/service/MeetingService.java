package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.DuplicationException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.domain.snap.service.SnapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private static final String LINK_PREFIX = "https://www.snappy.com/";
    private static final String DEFAULT_THUMBNAIL_URL = "https://dnd-11th-6.s3.ap-northeast-2.amazonaws.com/2024-08-04-2949758670_vN4YhHsL_3b0eb2d73a46652651648d805e4f3e859e776c0a.jpg";

    private final MeetingRepository meetingRepository;

    private final SnapService snapService;

    @Transactional(readOnly = true)
    public MeetingDetailResponseDto findByMeetingLink(String meetingLink) {
        Meeting meeting = findByMeetingLinkOrThrow(meetingLink);

        return new MeetingDetailResponseDto(meeting);
    }

    @Transactional
    public CreateMeetingResponseDto createMeeting(CreateMeetingRequestDto requestDto, MultipartFile thumbnail) {
        String meetingLink = generateMeetingLink();
        checkMeetingLinkDuplication(meetingLink);

        String thumbnailUrl = getThumbnailUrl(thumbnail);

        CreateMeetingEntityDto dto = createMeetingEntityDto(requestDto, meetingLink, thumbnailUrl);
        Meeting meeting = Meeting.create(dto);

        meetingRepository.save(meeting);

        return new CreateMeetingResponseDto(meetingLink);
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
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

    private String getThumbnailUrl(MultipartFile thumbnail) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            return snapService.upload(thumbnail);
        }
        return DEFAULT_THUMBNAIL_URL;
    }

    private CreateMeetingEntityDto createMeetingEntityDto(CreateMeetingRequestDto requestDto, String meetingLink, String thumbnailUrl) {
        return new CreateMeetingEntityDto(
                requestDto.name(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.description(),
                thumbnailUrl,
                requestDto.symbolColor(),
                requestDto.password(),
                requestDto.adminPassword(),
                meetingLink
        );
    }
}

