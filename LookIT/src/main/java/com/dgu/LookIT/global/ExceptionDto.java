package com.dgu.LookIT.global;

import com.dgu.LookIT.exception.ErrorCode;

public record ExceptionDto(
        Integer code,
        String message
) {
    public ExceptionDto(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }
}
