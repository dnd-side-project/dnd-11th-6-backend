package com.dnd.snappy.common.error.exception;

import com.dnd.snappy.common.error.ErrorCodeInterface;
import lombok.Getter;

@Getter
public class DuplicationException extends BusinessException {

    public DuplicationException(ErrorCodeInterface errorCode) {
        super(errorCode);
    }

    public DuplicationException(ErrorCodeInterface errorCode, String additionalMessage) {
        super(errorCode, additionalMessage);
    }

    public DuplicationException(ErrorCodeInterface errorCode, Long resourceId) {
        super(errorCode, "[resource: " + resourceId + " is duplicated]");
    }
}


