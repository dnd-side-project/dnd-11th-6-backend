package com.dnd.snappy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCodeInterface {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-001", "요청을 이해할 수 없거나 필수 매개변수가 누락되었습니다."),
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
