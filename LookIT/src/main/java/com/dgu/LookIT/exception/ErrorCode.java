package com.dgu.LookIT.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    WRONG_ENTRY_POINT(40000, HttpStatus.BAD_REQUEST, "잘못된 접근입니다"),
    MISSING_REQUEST_PARAMETER(40001, HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_PARAMETER_FORMAT(40002, HttpStatus.BAD_REQUEST, "요청에 유효하지 않은 인자 형식입니다."),
    BAD_REQUEST_JSON(40003, HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),

    //401
    INVALID_HEADER_VALUE(40100, HttpStatus.UNAUTHORIZED, "올바르지 않은 헤더값입니다."),
    TOKEN_EXPIRED_ERROR(40101, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_INVALID_ERROR(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_MALFORMED_ERROR(40103, HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
    TOKEN_TYPE_ERROR(40104, HttpStatus.UNAUTHORIZED, "토큰 타입이 일치하지 않거나 비어있습니다."),
    TOKEN_UNSUPPORTED_ERROR(40105, HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    TOKEN_GENERATION_ERROR(40106, HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
    TOKEN_UNKNOWN_ERROR(40107, HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다."),
    LOGIN_FAILURE(40108, HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다"),
    UNAUTHORIZED(40109, HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    //403
    FORBIDDEN_ROLE(40300, HttpStatus.FORBIDDEN, "권한이 존재하지 않습니다."),
    SELF_ACTION_NOT_ALLOWED(40301, HttpStatus.FORBIDDEN, "자신이 작성한 게시물이나 댓글에 대해 이 작업을 수행할 수 없습니다."),

    //404
    NOT_FOUND_USER(40400, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NOT_FOUND_POST(40401, HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_FOUND_TRAVEL(40402, HttpStatus.NOT_FOUND, "존재하지 않는 여행기록입니다."),
    NOT_FOUND_TRAVELDETAIL(40403, HttpStatus.NOT_FOUND, "존재하지 않는 여행장소입니다."),
    NOT_FOUND_COMMENT(40404, HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    NOT_FOUND_TEMP_PLACE(40405, HttpStatus.NOT_FOUND, "존재하지 않는 임시 여행장소입니다."),
    NOT_FOUND_CHAT_RECORD(40406, HttpStatus.NOT_FOUND, "존재하지 않는 채팅기록입니다."),

    //500
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다"),
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
