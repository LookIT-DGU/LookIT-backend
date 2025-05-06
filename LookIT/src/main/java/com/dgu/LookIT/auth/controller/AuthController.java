package com.dgu.LookIT.auth.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.auth.dto.request.SignUpRequest;
import com.dgu.LookIT.auth.dto.response.KakaoUserInfoResponse;
import com.dgu.LookIT.auth.service.AuthService;
import com.dgu.LookIT.auth.service.KakaoService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final KakaoService kakaoService;
    private final AuthService authService;

//    @GetMapping("/callback")
//    public ResponseEntity<?> callback(@RequestParam("code") String code) {
//        String accessToken = kakaoService.getAccessTokenFromKakao(code);
//        KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(accessToken);
//        log.info("User Info: {}", userInfo);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @GetMapping("/api/auth/login/kakao")
    public ResponseDto<?> login(@RequestParam("code") String code) {
        return ResponseDto.ok(authService.login(code));
    }

    @PatchMapping("/api/sign-up")
    public ResponseDto<?> signUp(@UserId Long userId, @RequestBody SignUpRequest signUpRequest) {
        return ResponseDto.created(authService.signUp(userId, signUpRequest));
    }

    @GetMapping("/api/test")
    public Long test(@UserId Long userId) {
        return userId;
    }
}
