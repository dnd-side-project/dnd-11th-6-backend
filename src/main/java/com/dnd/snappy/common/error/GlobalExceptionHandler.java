package com.dnd.snappy.common.error;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.ImageException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseDto<?>> handleBusinessException(
            final BusinessException e,
            final HttpServletRequest request
    ) {
        log.info("BusinessException: {} {}", e.getErrorCode(), request.getRequestURL());
        return ResponseDto.fail(e.getErrorCode());
    }

    @ExceptionHandler(ImageException.class)
    protected ResponseEntity<ResponseDto<?>> handleImageException(
            final ImageException e,
            final HttpServletRequest request
    ) {
        log.info("ImageException: {} {}", e.getErrorCode(), request.getRequestURL());
        return ResponseDto.fail(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseDto<?>> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e,
            final HttpServletRequest request
    ) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorCode errorCode = CommonErrorCode.VALIDATION_ERROR.toErrorCode();
        errorCode.appendMessage(errorMessage);

        log.info("ValidationException: {} {}", errorCode, request.getRequestURL());
        return ResponseDto.fail(errorCode);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ResponseDto<?>> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e,
            final HttpServletRequest request
    ) {
        ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST.toErrorCode();
        errorCode.appendMessage(e.getMessage());

        log.info("HttpMessageNotReadableException: {} {}", e.getMessage(), request.getRequestURL(), e);
        return ResponseDto.fail(errorCode);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<?>> handleBusinessException(
            final Exception e,
            final HttpServletRequest request
    ) {
        log.error("[Exception] 예상치 못한 오류 발생: {} {}", e.getMessage(), request.getRequestURL(), e);
        return ResponseDto.fail(CommonErrorCode.INTERNAL_SERVER_ERROR.toErrorCode());
    }

}
