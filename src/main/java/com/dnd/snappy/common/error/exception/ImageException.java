package com.dnd.snappy.common.error.exception;

import com.dnd.snappy.common.error.ErrorCodeInterface;

public class ImageException extends BusinessException {

    public ImageException(ErrorCodeInterface errorCode) {
        super(errorCode);
    }

    public ImageException(ErrorCodeInterface errorCode, String additionalMessage) {
        super(errorCode, additionalMessage);
    }
}

