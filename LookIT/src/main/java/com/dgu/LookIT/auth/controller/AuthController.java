package com.dgu.LookIT.auth.controller;

import com.dgu.LookIT.auth.dto.response.KakaoUserInfoResponse;
import com.dgu.LookIT.auth.service.KakaoService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final KakaoService kakaoService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(accessToken);
        log.info("User Info: {}", userInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
