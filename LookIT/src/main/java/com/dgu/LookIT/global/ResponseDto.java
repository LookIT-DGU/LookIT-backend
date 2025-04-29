package com.dgu.LookIT.global;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public record ResponseDto<T>(
        @JsonIgnore HttpStatus httpStatus,
        boolean success,
        @Nullable T data,
        @Nullable ExceptionDto error
) {
    public static <T> ResponseDto<T> ok(T data) {
        return new ResponseDto<>(
                HttpStatus.OK,
                true,
                data,
                null
        );
    }
    public static ResponseDto<Boolean> created(Boolean data){
        return new ResponseDto<>(
                HttpStatus.CREATED,
                true,
                data,
                null
        );
    }
    public static ResponseDto<?> fail(@NotNull CommonException e){
        return new ResponseDto<>(
                e.getErrorCode().getHttpStatus(),
                false,
                null,
                new ExceptionDto(e.getErrorCode())
        );
    }

    public static ResponseDto<?> fail(final MissingServletRequestParameterException e) {
        return new ResponseDto<>(
                HttpStatus.BAD_REQUEST,
                false,
                null,
                new ExceptionDto(ErrorCode.MISSING_REQUEST_PARAMETER)
        );
    }

    public static ResponseDto<?> fail(final MethodArgumentTypeMismatchException e) {
        return new ResponseDto<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                false,
                null,
                new ExceptionDto(ErrorCode.INVALID_PARAMETER_FORMAT)
        );
    }
}
