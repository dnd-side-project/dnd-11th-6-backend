package com.dnd.snappy.common.dto;

import com.dnd.snappy.common.error.ErrorCodeInterface;
import org.springframework.http.ResponseEntity;

public record ErrorResponseDto(int status, String code, String message) {

    public static ErrorResponseDto from(ErrorCodeInterface errorCode) {
        return new ErrorResponseDto(errorCode.getStatus().value(), errorCode.getErrorResponseCode(), errorCode.getMessage());
    }

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(ErrorCodeInterface errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponseDto.from(errorCode));
    }
}
