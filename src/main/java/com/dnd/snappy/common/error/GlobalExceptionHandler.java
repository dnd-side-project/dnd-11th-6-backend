package com.dnd.snappy.common.error;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.common.error.exception.BusinessException;
import com.dnd.snappy.common.error.exception.S3Exception;
import com.dnd.snappy.domain.meeting.exception.MeetingErrorCode;
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

        ErrorCode errorCode = MeetingErrorCode.VALIDATION_ERROR.toErrorCode();
        errorCode.appendMessage(errorMessage);

        log.error("ValidationException: {} {}", errorCode, request.getRequestURL());
        return ResponseDto.fail(errorCode);
    }

    @ExceptionHandler(S3Exception.class)
    protected ResponseEntity<ResponseDto<?>> handleS3Exception(
            final S3Exception e,
            final HttpServletRequest request
    ) {
        log.error("S3Exception: {} {}", e.getErrorCode(), request.getRequestURL());
        return ResponseDto.fail(e.getErrorCode());
    }

}
