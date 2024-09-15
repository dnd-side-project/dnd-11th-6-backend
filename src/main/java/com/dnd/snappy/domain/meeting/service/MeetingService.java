package com.dnd.snappy.domain.meeting.service;

import static com.dnd.snappy.domain.meeting.exception.MeetingErrorCode.*;

import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.DuplicationException;
import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingEntityDto;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.meeting.dto.response.CreateMeetingResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.MeetingDetailResponseDto;
import com.dnd.snappy.domain.meeting.dto.response.ShareMeetingLinkResponseDto;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.entity.MeetingLinkStatus;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
import com.dnd.snappy.domain.meeting.repository.MeetingRepository;
import com.dnd.snappy.infrastructure.uploader.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {
    private static final String DEFAULT_THUMBNAIL_URL = "logo.png";

    private final MeetingRepository meetingRepository;

    private final ImageUploader imageUploader;

    @Transactional(readOnly = true)
    public MeetingDetailResponseDto findByMeetingLink(String meetingLink) {
        Meeting meeting = findByMeetingLinkOrThrow(meetingLink);

        validateMeetingStatus(meeting);

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
    public void validateMeetingLeaderAuthKey(Long meetingId, String leaderAuthKey) {
        Meeting meeting = findByMeetingIdOrThrow(meetingId);

        if(!meeting.isCorrectLeaderAuthKey(leaderAuthKey)) {
            throw new BusinessException(MEETING_INVALIDATE_AUTH_KEY);
        }
    }

    @Transactional(readOnly = true)
    public ShareMeetingLinkResponseDto getShareableMeetingLink(Long meetingId) {
        Meeting meeting = findByMeetingIdOrThrow(meetingId);
        String meetingLink = meeting.getMeetingLink();
        return new ShareMeetingLinkResponseDto(meetingLink);
    }

    @Transactional
    public CreateMeetingResponseDto createMeeting(CreateMeetingRequestDto requestDto, MultipartFile thumbnail) {
        String meetingLinkUuid = generateMeetingLink();
        String thumbnailUrl = getThumbnailUrl(thumbnail);

        checkMeetingLinkDuplication(meetingLinkUuid);

        String leaderAuthKey = generateLeaderAuthKey();

        CreateMeetingEntityDto dto = CreateMeetingEntityDto.of(requestDto, thumbnailUrl, meetingLinkUuid, leaderAuthKey);
        Meeting meeting = Meeting.create(dto);
        meetingRepository.save(meeting);

        return new CreateMeetingResponseDto(meeting);
    }

    public MeetingDetailResponseDto findMeetingDetailById(Long meetingId) {
        Meeting meeting = findByMeetingIdOrThrow(meetingId);

        validateMeetingStatus(meeting);

        return new MeetingDetailResponseDto(meeting);
    }

    public Meeting findByMeetingIdOrThrow(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND, meetingId));
    }

    private Meeting findByMeetingLinkOrThrow(String meetingLink) {
        return meetingRepository.findByMeetingLink(meetingLink)
                .orElseThrow(() -> new NotFoundException(MEETING_LINK_NOT_FOUND, "[meetingLink: " + meetingLink + " is not found]"));
    }

    private void validateMeetingStatus(Meeting meeting) {
        if (meeting.getMeetingLinkStatus() == MeetingLinkStatus.EXPIRED) {
            throw new BusinessException(MeetingErrorCode.MEETING_LINK_EXPIRED);
        }
    }

    private void checkMeetingLinkDuplication(String meetingLink) {
        if (meetingRepository.existsByMeetingLink(meetingLink)) {
            throw new DuplicationException(DUPLICATION_MEETING_LINK);
        }
    }

    private String generateMeetingLink() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    private String getThumbnailUrl(MultipartFile thumbnail) {
        if (thumbnail != null && !thumbnail.isEmpty()) {
            return imageUploader.upload(thumbnail);
        }
        return DEFAULT_THUMBNAIL_URL;
    }

    private static String generateLeaderAuthKey() {
        return String.format("%04d", new Random().nextInt(10000));
    }
}
