package com.dnd.snappy.controller.v1.snap;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.controller.v1.auth.resolver.AuthInfo;
import com.dnd.snappy.controller.v1.auth.resolver.AuthPrincipal;
import com.dnd.snappy.controller.v1.snap.request.CreateSnapRequest;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.service.SimpleSnapService;
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

    private final SimpleSnapService simpleSnapService;

    @PostMapping("/simple")
    public ResponseEntity<ResponseDto<CreateSnapResponseDto>> createSimpleSnap(
            @PathVariable("meetingId") Long meetingId,
            @AuthPrincipal AuthInfo authInfo,
            @RequestPart("snap") @Valid CreateSnapRequest createSnapshotRequest,
            @RequestPart("file") MultipartFile snap
    ) {
        var data = simpleSnapService.create(meetingId, authInfo.participantId(), snap, createSnapshotRequest.shootDate());
        return ResponseDto.created(data);
    }
}
