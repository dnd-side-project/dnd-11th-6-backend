package com.dnd.snappy.domain.meeting.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MeetingErrorCode implements ErrorCodeInterface {
    MEETING_LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING_LINK_NOT_FOUND", "요청된 모임 링크를 찾을 수 없습니다."),
    DUPLICATION_MEETING_LINK(HttpStatus.CONFLICT, "DUPLICATION_MEETING_LINK", "모임 링크가 중복되었습니다."),
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
