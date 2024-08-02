package com.dnd.snappy.common.error.exception;

import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.Getter;

@Getter
public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCodeInterface errorCode) {
        super(errorCode.toErrorCode());
    }

    public NotFoundException(ErrorCodeInterface errorCode, String additionalMessage) {
        super(errorCode.toErrorCode(), additionalMessage);
    }

    public NotFoundException(ErrorCodeInterface errorCode, Long id) {
        super(errorCode.toErrorCode(), "[id: " + id + " is not found]");
    }

}
