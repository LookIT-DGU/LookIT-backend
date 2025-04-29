package com.dgu.LookIT.security.handler;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.global.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {  //인증 실패시 처리
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");
        if (errorCode == null)
            errorCode = ErrorCode.WRONG_ENTRY_POINT;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        response.getWriter().write(
                objectMapper.writeValueAsString(ResponseDto.fail(new CommonException(errorCode))) // 수정 필요
        );
    }
}
