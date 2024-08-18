package com.dnd.snappy.domain.mission.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements ErrorCodeInterface {
    NOT_FOUND_RANDOM_MISSION(HttpStatus.NOT_FOUND, "NOT_FOUND_RANDOM_MISSION", "랜덤 미션을 찾을 수 없습니다."),
    NOT_FOUND_MEETING_MISSION(HttpStatus.NOT_FOUND, "NOT_FOUND_RANDOM_MISSION", "모임 미션을 찾을 수 없습니다."),
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
