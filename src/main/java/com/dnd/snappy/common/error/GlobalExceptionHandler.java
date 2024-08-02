package com.dnd.snappy.common.error;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.common.error.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseDto<?>> handleBusinessException(
            final BusinessException e,
            final HttpServletRequest request
    ) {
        log.error("BusinessException: {} {}", e.getErrorCode(), request.getRequestURL());
        return ResponseDto.fail(e.getErrorCode());
    }
}
