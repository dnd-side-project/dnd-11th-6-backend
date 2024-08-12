package com.dnd.snappy.domain.auth.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCodeInterface {
    JWT_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "JWT_EXPIRED_ERROR", "jwt 토큰의 유효기간이 지났습니다."),
    JWT_EXTRACT_ERROR(HttpStatus.UNAUTHORIZED, "JWT_EXTRACT_ERROR", "jwt 토큰을 추출하는데 문제가 생겼습니다."),
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
