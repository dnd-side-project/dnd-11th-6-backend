package com.dnd.snappy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCodeInterface {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_BAD_REQUEST", "요청을 이해할 수 없거나 필수 매개변수가 누락되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_NOT_FOUND", "요청된 리소스를 찾을 수 없습니다."),

    DUPLICATION(HttpStatus.CONFLICT, "COMMON_DUPLICATION", "중복된 리소스가 존재합니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "유효성 검사 실패했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청 형식에 맞지 않는 요청입니다. api 문서를 다시 확인해 주세요."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "알 수 없는 에러")
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
