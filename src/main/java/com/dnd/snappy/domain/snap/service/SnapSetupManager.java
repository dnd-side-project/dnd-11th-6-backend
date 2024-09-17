package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.image.Folder;
import com.dnd.snappy.domain.image.ImageFile;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.infrastructure.image.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class SnapSetupManager {
    private final MeetingService meetingService;
    private final ImageUploader imageUploader;
    private final ParticipantRepository participantRepository;

    public SnapSetupDto setup(Long meetingId, Long participantId, MultipartFile file) {
        Meeting meeting = meetingService.findByMeetingIdOrThrow(meetingId);
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ParticipantErrorCode.NOT_FOUND_PARTICIPANT, participantId));
        String url = imageUploader.upload(new ImageFile(file, Folder.SNAP));
        return new SnapSetupDto(meeting, participant, url);
    }
}
