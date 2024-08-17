package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.meeting.service.MeetingService;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.participant.exception.ParticipantErrorCode;
import com.dnd.snappy.domain.participant.repository.ParticipantRepository;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import com.dnd.snappy.infrastructure.uploader.ImageUploader;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SnapService {

    private final SnapRepository snapRepository;

    private final ParticipantRepository participantRepository;

    private final MeetingService meetingService;

    private final ImageUploader imageUploader;


    public CreateSnapResponseDto createSnap(Long meetingId, Long participantId, MultipartFile file, LocalDateTime shootDate) {
        Meeting meeting = meetingService.findByMeetingIdOrThrow(meetingId);
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ParticipantErrorCode.NOT_FOUND_PARTICIPANT, participantId));
        String url = imageUploader.upload(file);

        Snap snap = Snap.createSnap(meeting, participant, url, shootDate);
        snapRepository.save(snap);

        return new CreateSnapResponseDto(
                snap.getId(),
                snap.getSnapUrl()
        );
    }
}
