package com.dnd.snappy.domain.snap.service;

import com.dnd.snappy.domain.common.BaseEntity;
import com.dnd.snappy.domain.common.dto.request.CursorBasedRequestDto;
import com.dnd.snappy.domain.common.dto.response.CursorBasedResponseDto;
import com.dnd.snappy.domain.snap.dto.response.SnapResponseDto;
import com.dnd.snappy.domain.snap.entity.Snap;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SnapService {

    private final SnapRepository snapRepository;
//
//    public CursorBasedResponseDto<List<SnapResponseDto>> findSnapsInMeeting(CursorBasedRequestDto cursorBasedRequestDto, Long meetingId) {
//        Long count = snapRepository.countByMeetingId(meetingId);
//        if (count == 0L) {
//            return CursorBasedResponseDto.empty();
//        }
//
//        Long cursorId = getCursorId(cursorBasedRequestDto.cursorId(), meetingId);
//        PageRequest pageable = PageRequest.of(0, cursorBasedRequestDto.limit());
//
//        List<SnapResponseDto> snapResponse = snapRepository.findSnapsInMeetingByCursorId(cursorId, meetingId, pageable);
//        if(snapResponse.isEmpty()){
//            return CursorBasedResponseDto.empty();
//        }
//
//        SnapResponseDto lastSnapResponse = snapResponse.get(snapResponse.size() - 1);
//        return CursorBasedResponseDto.of(
//                lastSnapResponse.snapId(),
//                snapResponse,
//                count,
//                cursorBasedRequestDto.limit() > snapResponse.size()
//        );
//    }

    private Long getCursorId(Optional<Long> cursorId, Long meetingId) {
        return cursorId.orElseGet(() ->
                        snapRepository.findFirstByMeetingIdOrderByIdDesc(meetingId)
                                .map(BaseEntity::getId)
                                .orElse(0L));
    }
}
