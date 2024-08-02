package com.dnd.snappy.common.dto;

import com.dnd.snappy.common.error.ErrorCodeInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ResponseDto<T>(
        boolean success,
        T data,
        ErrorResponseDto error
) {

    public static <T> ResponseEntity<ResponseDto<T>> ok(T data) {
        ResponseDto<T> responseDto = new ResponseDto<>(true, data, null);
        return ResponseEntity
                .ok(responseDto);
    }

    public static <T> ResponseEntity<ResponseDto<?>> ok() {
        ResponseDto<T> responseDto = new ResponseDto<>(true, null, null);
        return ResponseEntity
                .ok(responseDto);
    }

    public static <T> ResponseEntity<ResponseDto<T>> created(T data) {
        ResponseDto<T> responseDto = new ResponseDto<>(true, data, null);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    public static ResponseEntity<ResponseDto<?>> fail(ErrorCodeInterface errorCode) {
        ResponseDto<?> responseDto = new ResponseDto<>(false, null, ErrorResponseDto.from(errorCode));
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(responseDto);
    }
}
