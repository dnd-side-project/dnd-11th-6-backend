package com.dnd.snappy.domain.mission.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MissionErrorCode implements ErrorCodeInterface {
    UNAUTHORIZED_MISSION(HttpStatus.FORBIDDEN, "UNAUTHORIZED_MISSION", "모임 미션 생성/수정/삭제 권한이 없습니다."),
    MISSION_HAS_PARTICIPANTS(HttpStatus.FORBIDDEN, "MISSION_HAS_PARTICIPANTS", "미션에 참여자가 있어서 수정/삭제할 수 없습니다."),
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION_NOT_FOUND", "미션이 존재하지 않습니다."),
    MISSION_CONTENT_UNCHANGED(HttpStatus.BAD_REQUEST, "MISSION_CONTENT_UNCHANGED", "변경된 사항이 없습니다."),
    MISSION_NOT_FOUND_FOR_MEETING(HttpStatus.NOT_FOUND, "MISSION_NOT_FOUND_FOR_MEETING", "해당 모임에 소속된 미션이 존재하지 않습니다.")
      
    NOT_FOUND_RANDOM_MISSION(HttpStatus.NOT_FOUND, "NOT_FOUND_RANDOM_MISSION", "랜덤 미션을 찾을 수 없습니다."),
    NOT_FOUND_MEETING_MISSION(HttpStatus.NOT_FOUND, "NOT_FOUND_RANDOM_MISSION", "모임 미션을 찾을 수 없습니다."),
    ALREADY_COMPLETED_MEETING_MISSION(HttpStatus.BAD_REQUEST, "ALREADY_COMPLETED_MEETING_MISSION", "이미 수행한 모임 미션입니다.");
    ;

    private final HttpStatus status;
    private final String errorResponseCode;
    private final String message;

    @Override
    public ErrorCode toErrorCode() {
        return ErrorCode.builder()
                .status(status)
                .errorResponseCode(errorResponseCode)
                .message(message)
                .build();
    }
}
