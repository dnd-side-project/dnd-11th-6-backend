package com.dnd.snappy.infrastructure.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageErrorCode implements ErrorCodeInterface {
    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, "IMAGE_EMPTY_FILE_EXCEPTION", "파일이 비어 있습니다."),
    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "IMAGE_NO_FILE_EXTENSION", "파일 확장자가 없습니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "IMAGE_INVALID_FILE_EXTENSION", "잘못된 파일 확장자입니다."),
    IO_EXCEPTION_ON_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_IO_EXCEPTION_ON_UPLOAD", "업로드 중 오류가 발생했습니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_PUT_OBJECT_EXCEPTION", "객체를 업로드하는 중 오류가 발생했습니다."),
    IO_EXCEPTION_ON_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_IO_EXCEPTION_ON_DELETE", "삭제 중 오류가 발생했습니다.");

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


