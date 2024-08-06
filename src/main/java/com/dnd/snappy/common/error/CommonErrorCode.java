package com.dnd.snappy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCodeInterface {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_BAD_REQUEST", "요청을 이해할 수 없거나 필수 매개변수가 누락되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_NOT_FOUND", "요청된 리소스를 찾을 수 없습니다."),



    JWT_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "JWT_EXPIRED_ERROR", "jwt 토큰의 유효기간이 지났습니다."),
    JWT_EXTRACT_ERROR(HttpStatus.UNAUTHORIZED, "JWT_EXTRACT_ERROR", "jwt 토큰을 추출하는데 문제가 생겼습니다.")
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
