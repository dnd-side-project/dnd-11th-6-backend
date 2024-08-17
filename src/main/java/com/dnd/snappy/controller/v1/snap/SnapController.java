package com.dnd.snappy.controller.v1.snap;

import com.amazonaws.services.ec2.model.CreateSnapshotRequest;
import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.controller.v1.snap.request.CreateSnapRequest;
import com.dnd.snappy.domain.meeting.dto.request.CreateMeetingRequestDto;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.service.SnapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/meetings/{meetingId}/snaps")
@RequiredArgsConstructor
public class SnapController {

    private final SnapService snapService;

    @PostMapping
    public ResponseEntity<ResponseDto<CreateSnapResponseDto>> createSnap(
            @PathVariable("meetingId") Long meetingId,
            @AuthPrincipal AuthInfo authInfo,
            @RequestPart("snap") @Valid CreateSnapRequest createSnapshotRequest,
            @RequestPart("file") MultipartFile snap
    ) {
        var data = snapService.createSnap(meetingId, authInfo.participantId(), snap, createSnapshotRequest.shootDate());
        return ResponseDto.created(data);
    }
}
