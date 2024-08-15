package com.dnd.snappy.common.error.exception;

import com.dnd.snappy.common.error.ErrorCode;
import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCodeInterface errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.toErrorCode();
    }

    public BusinessException(ErrorCodeInterface errorCode, String additionalMessage) {
        super(errorCode.getMessage() + additionalMessage);
        this.errorCode = errorCode.toErrorCode();
        this.errorCode.appendMessage(additionalMessage);
    }
}
