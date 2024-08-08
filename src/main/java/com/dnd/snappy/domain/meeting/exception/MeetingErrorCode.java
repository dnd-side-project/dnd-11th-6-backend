package com.dnd.snappy.domain.meeting.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MeetingErrorCode implements ErrorCodeInterface {
    MEETING_LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING_LINK_NOT_FOUND", "요청된 모임 링크를 찾을 수 없습니다."),
    DUPLICATION(HttpStatus.CONFLICT, "COMMON_DUPLICATION", "중복된 리소스가 존재합니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "유효성 검사 실패했습니다."),
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
