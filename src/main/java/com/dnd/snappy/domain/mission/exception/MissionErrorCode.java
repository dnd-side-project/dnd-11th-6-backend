package com.dnd.snappy.domain.mission.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MissionErrorCode implements ErrorCodeInterface {
    UNAUTHORIZED_MISSION_CREATION(HttpStatus.FORBIDDEN, "UNAUTHORIZED_MISSION_CREATION", "미션 생성 권한이 없습니다."),
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
