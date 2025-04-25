package com.dgu.LookIT.global;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

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
}
