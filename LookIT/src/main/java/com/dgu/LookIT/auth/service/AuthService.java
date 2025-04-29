package com.dgu.LookIT.auth.service;

import com.dgu.LookIT.auth.dto.request.SignUpRequest;
import com.dgu.LookIT.auth.dto.response.AuthorizationResponse;
import com.dgu.LookIT.auth.dto.response.KakaoUserInfoResponse;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.user.domain.User;
import com.dgu.LookIT.user.repository.UserRepository;
import com.dgu.LookIT.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthorizationResponse login(String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponse kakaoUserInfoResponse = kakaoService.getUserInfo(accessToken);
        Long serialId = kakaoUserInfoResponse.getId();

        User user = userRepository.findBySerialId(serialId).orElseGet(
                () -> userRepository.save(new User(serialId))
        );
        return AuthorizationResponse.of(user.getName() != null, jwtUtil.generateTokens(user.getId()));
    }

    @Transactional
    public Boolean signUp(Long userId, SignUpRequest signUpRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        user.signUpUser(signUpRequest);

        return Boolean.TRUE;
    }

}
