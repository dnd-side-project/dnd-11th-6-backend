package com.dnd.snappy.common.dto;

import com.dnd.snappy.common.error.ErrorCodeInterface;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ResponseDto<T>(
        int status,
        T data,
        @JsonInclude(JsonInclude.Include.NON_NULL) ErrorResponseDto error
) {

    public static <T> ResponseEntity<ResponseDto<T>> ok(T data) {
        return ResponseEntity
                .ok(new ResponseDto<>(HttpStatus.OK.value(), data, null));
    }

    public static <T> ResponseEntity<ResponseDto<?>> ok() {
        return ResponseEntity
                .ok(new ResponseDto<>(HttpStatus.OK.value(), null, null));
    }

    public static <T> ResponseEntity<ResponseDto<T>> created(T data) {
        ResponseDto<T> responseDto = new ResponseDto<>(HttpStatus.CREATED.value(), data, null);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    public static ResponseEntity<ResponseDto<?>> fail(ErrorCodeInterface errorCode) {
        ResponseDto<?> responseDto = new ResponseDto<>(errorCode.getStatus().value(), null, ErrorResponseDto.from(errorCode));
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(responseDto);
    }
}

