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
    MEETING_INVALIDATE_AUTH_KEY(HttpStatus.BAD_REQUEST, "MEETING_INVALIDATE_AUTH_KEY", "모임의 관리자 인증키가 유효하지 않습니다."),
    MEETING_JOIN_DENIED(HttpStatus.FORBIDDEN, "MEETING_JOIN_DENIED", "이미 끝난 모임에는 참여할 수 없습니다."),
    DUPLICATION_MEETING_LINK(HttpStatus.CONFLICT, "DUPLICATION_MEETING_LINK", "모임 링크가 중복되었습니다."),
    NO_IN_PROGRESS_MEETING(HttpStatus.FORBIDDEN, "NO_IN_PROGRESS_MEETING", "진행중인 모임이 아닙니다."),
    UNAUTHORIZED_MEETING(HttpStatus.FORBIDDEN, "UNAUTHORIZED_MEETING", "모임 수정 권한이 없습니다."),
    NO_MODIFICATION(HttpStatus.BAD_REQUEST, "NO_MODIFICATION", "변경된 수정 사항이 없습니다.");

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
