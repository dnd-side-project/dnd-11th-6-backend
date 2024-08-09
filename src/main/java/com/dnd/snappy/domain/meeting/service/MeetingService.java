package com.dnd.snappy.domain.meeting.service;

import com.dnd.snappy.common.error.exception.DuplicationException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.infrastructure.uploader.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private static final String DEFAULT_THUMBNAIL_URL = "https://dnd-11th-6.s3.ap-northeast-2.amazonaws.com/logo.png";

    private final MeetingRepository meetingRepository;

    private final ImageUploader imageUploader;

    @Transactional(readOnly = true)
    public MeetingDetailResponseDto findByMeetingLink(String meetingLink) {
        Meeting meeting = findByMeetingLinkOrThrow(meetingLink);
        return new MeetingDetailResponseDto(meeting);
    }

    @Transactional
    public CreateMeetingResponseDto createMeeting(CreateMeetingRequestDto requestDto, MultipartFile thumbnail) {
        String meetingLinkUuid = generateMeetingLink();
        checkMeetingLinkDuplication(meetingLinkUuid);

        String thumbnailUrl = getThumbnailUrl(thumbnail);

        CreateMeetingEntityDto dto = CreateMeetingEntityDto.of(requestDto, thumbnailUrl, meetingLinkUuid);
        Meeting meeting = Meeting.create(dto);

        meetingRepository.save(meeting);
        return new CreateMeetingResponseDto(meetingLinkUuid);
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MeetingErrorCode.MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
    }

    private void checkMeetingLinkDuplication(String meetingLink) {
        if (meetingRepository.existsByMeetingLink(meetingLink)) {
            throw new DuplicationException(MeetingErrorCode.DUPLICATION_MEETING_LINK, "모임 링크가 중복되었습니다.");
        }
    }

    private String generateMeetingLink() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "").substring(0, 7);
    }

    private String getThumbnailUrl(MultipartFile thumbnail) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            return imageUploader.upload(thumbnail);
        }
        return DEFAULT_THUMBNAIL_URL;
    }
}
