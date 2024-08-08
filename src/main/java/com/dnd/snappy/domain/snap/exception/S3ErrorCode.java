package com.dnd.snappy.domain.snap.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements ErrorCodeInterface {
    EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, "S3_EMPTY_FILE_EXCEPTION", "파일이 비어 있습니다."),
    NO_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S3_NO_FILE_EXTENSION", "파일 확장자가 없습니다."),
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S3_INVALID_FILE_EXTENSION", "잘못된 파일 확장자입니다."),
    IO_EXCEPTION_ON_SNAP_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "S3_IO_EXCEPTION_ON_SNAP_UPLOAD", "스냅 업로드 중 오류가 발생했습니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S3_PUT_OBJECT_EXCEPTION", "S3에 객체를 업로드하는 중 오류가 발생했습니다."),
    IO_EXCEPTION_ON_SNAP_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "S3_IO_EXCEPTION_ON_SNAP_DELETE", "스냅 삭제 중 오류가 발생했습니다.");

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


