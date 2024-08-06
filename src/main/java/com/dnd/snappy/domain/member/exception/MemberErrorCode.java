package com.dnd.snappy.domain.member.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCodeInterface {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "member를 찾을 수 없습니다."),
    ALREADY_PARTICIPATE_MEETING(HttpStatus.BAD_REQUEST, "ALREADY_PARTICIPATE_MEETING", "이미 참여중인 모임입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "DUPLICATED_NICKNAME", "모임에서 사용중인 닉네임입니다.")
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
