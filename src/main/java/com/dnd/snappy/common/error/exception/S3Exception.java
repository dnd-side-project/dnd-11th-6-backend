package com.dnd.snappy.common.error.exception;

import com.dnd.snappy.common.error.ErrorCodeInterface;

public class S3Exception extends BusinessException {

    public S3Exception(ErrorCodeInterface errorCode) {
        super(errorCode);
    }

    public S3Exception(ErrorCodeInterface errorCode, String additionalMessage) {
        super(errorCode, additionalMessage);
    }
}

