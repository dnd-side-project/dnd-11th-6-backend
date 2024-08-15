package com.dnd.snappy.common.error;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.ImageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ResponseDto<?>> handleBusinessException(
            final RuntimeException e,
            final HttpServletRequest request
    ) {
        log.error("RuntimeException: {} {}", e.getMessage(), request.getRequestURL());
        return ResponseDto.fail(CommonErrorCode.INTERNAL_SERVER_ERROR.toErrorCode());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ResponseDto<?>> handleBusinessException(
            final BusinessException e,
            final HttpServletRequest request
    ) {
        log.error("BusinessException: {} {}", e.getErrorCode(), request.getRequestURL());
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

        log.error("ValidationException: {} {}", errorCode, request.getRequestURL());
        return ResponseDto.fail(errorCode);
    }

    @ExceptionHandler(ImageException.class)
    protected ResponseEntity<ResponseDto<?>> handleImageException(
            final ImageException e,
            final HttpServletRequest request
    ) {
        log.error("ImageException: {} {}", e.getErrorCode(), request.getRequestURL());
        return ResponseDto.fail(e.getErrorCode());
    }

}
