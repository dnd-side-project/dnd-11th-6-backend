package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.SimpleSnap;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SimpleSnapService {

    private final SnapSetupManager snapSetupManager;

    private final SnapRepository snapRepository;


    public CreateSnapResponseDto create(Long meetingId, Long participantId, MultipartFile file, LocalDateTime shootDate) {
        SnapSetupDto snapSetupDto = snapSetupManager.setup(meetingId, participantId, file);

        Snap snap = SimpleSnap.create(snapSetupDto.snapUrl(), shootDate, snapSetupDto.meeting(), snapSetupDto.participant());
        snapRepository.save(snap);

        return new CreateSnapResponseDto(
                snap.getId(),
                snap.getSnapUrl()
        );
    }
}
