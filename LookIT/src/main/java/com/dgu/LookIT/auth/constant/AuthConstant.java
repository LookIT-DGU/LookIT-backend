package com.dgu.LookIT.auth.constant;

public class AuthConstant {
    public static final String USER_ID_CLAIM_NAME = "uid";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String[] AUTH_WHITELIST = {
            "/kakao_login_medium_narrow.png",
            "/static/**",
            "/login/page",
            "/api/auth/login/kakao/**",
            "/api/auth/login/kakao",
    };
    private AuthConstant() {
    }
}
