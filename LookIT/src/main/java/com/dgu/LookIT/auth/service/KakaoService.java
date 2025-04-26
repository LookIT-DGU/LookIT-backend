package com.dgu.LookIT.auth.service;

import com.dgu.LookIT.auth.dto.response.KakaoTokenResponse;
import com.dgu.LookIT.auth.dto.response.KakaoUserInfoResponse;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {
    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;

    @Autowired
    public KakaoService(@Value("${spring.kakao.client_id}") String clientId) {
        this.clientId = clientId;
        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponse kakaoTokenResponse = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new CommonException(ErrorCode.INVALID_PARAMETER_FORMAT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR)))
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponse.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponse.getRefreshToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponse.getScope());

        return kakaoTokenResponse.getAccessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {

        KakaoUserInfoResponse userInfoResponse = WebClient.create(KAUTH_USER_URL_HOST).get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new CommonException(ErrorCode.INVALID_PARAMETER_FORMAT)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new CommonException(ErrorCode.INTERNAL_SERVER_ERROR)))
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfoResponse.getId());
        log.info(" [Kakao Service] Created At ------> {}", userInfoResponse.getConnectedAt());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfoResponse.getKakaoAccount().getProfile().getNickname());

        return userInfoResponse;
    }

}
