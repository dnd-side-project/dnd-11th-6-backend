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
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING_NOT_FOUND", "요청된 모임을 찾을 수 없습니다."),
    MEETING_INVALIDATE_PASSWORD(HttpStatus.BAD_REQUEST, "MEETING_INVALIDATE_PASSWORD", "모임의 비밀번호가 유효하지 않습니다."),
    MEETING_JOIN_DENIED(HttpStatus.FORBIDDEN, "MEETING_JOIN_DENIED", "이미 끝난 모임에는 참여할 수 없습니다.")
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
