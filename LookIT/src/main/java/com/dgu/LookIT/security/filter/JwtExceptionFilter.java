package com.dgu.LookIT.security.filter;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (MalformedJwtException e) {
            handleException(request, response, filterChain, ErrorCode.TOKEN_MALFORMED_ERROR, e);
        } catch (IllegalArgumentException e) {
            handleException(request, response, filterChain, ErrorCode.TOKEN_TYPE_ERROR, e);
        } catch (ExpiredJwtException e) {
            handleException(request, response, filterChain, ErrorCode.TOKEN_EXPIRED_ERROR, e);
        } catch (UnsupportedJwtException e) {
            handleException(request, response, filterChain, ErrorCode.TOKEN_UNSUPPORTED_ERROR, e);
        } catch (JwtException e) {
            handleException(request, response, filterChain, ErrorCode.TOKEN_UNKNOWN_ERROR, e);
        } catch (CommonException e) {
            handleException(request, response, filterChain, e.getErrorCode(), e);
        } catch (Exception e) {
            handleException(request, response, filterChain, ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    private void handleException(   //예외 처리
                                    final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain,
                                    final ErrorCode errorCode,
                                    final Exception e
    ) throws ServletException, IOException {
        log.error(e.getMessage(), e);
        request.setAttribute("exception", errorCode);
        filterChain.doFilter(request, response);
    }
}

