package com.dnd.snappy.domain.auth.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCodeInterface {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN" ,"접근할 수 없습니다.")
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
