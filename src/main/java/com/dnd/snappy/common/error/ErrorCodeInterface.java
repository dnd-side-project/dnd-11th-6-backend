package com.dnd.snappy.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCodeInterface {

    HttpStatus getStatus();

    String getErrorResponseCode();

    String getMessage();

    ErrorCode toErrorCode();

}
