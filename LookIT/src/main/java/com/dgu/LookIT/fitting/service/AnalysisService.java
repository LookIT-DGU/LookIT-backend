package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.fitting.domain.FaceMood;
import com.dgu.LookIT.fitting.domain.FaceShape;
import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import com.dgu.LookIT.user.domain.User;
import com.dgu.LookIT.user.repository.UserRepository;
import com.dgu.LookIT.util.UserAnalysisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisService {

    private final UserAnalysisCacheHelper userAnalysisCacheHelper;
    private final StyleAnalysisRepository styleAnalysisRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;

    @Transactional
    public void postBodyType(Long userId, String bodyAnalysis) {
        userAnalysisCacheHelper.setBodyType(userId, bodyAnalysis);
    }
    @Transactional
    public void postFaceMood(Long userId, String faceMood) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Optional<StyleAnalysis> existing = styleAnalysisRepository.findByUser(user);
        StyleAnalysis analysis = existing.orElseGet(() -> new StyleAnalysis(userId));

        try {
            FaceMood mood = FaceMood.valueOf(faceMood.toUpperCase()); // 대소문자 처리 필요시
            analysis.setFaceMood(mood);
            styleAnalysisRepository.save(analysis);
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_FACE_MOOD);
        }
    }

    @Async
    public void processFaceAnalysisAsync(Long userId, MultipartFile faceImage) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("faceImageUrl", faceImage.getResource())
                    .filename(faceImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            webClient.post()
                    .uri("/face-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        // 요청 자체가 실패한 경우 (예: 호스트 없음, 서버 다운 등)
                        log.error("[요청 실패] 얼굴형 분석 AI 서버 연결 실패: {}", e.getMessage(), e);
                    })
                    .subscribe(faceShapeStr -> {
                        // 정상 응답 처리
                        if (faceShapeStr == null || faceShapeStr.isBlank()) {
                            log.error("[응답 실패] AI 서버에서 받은 얼굴형 응답이 비어 있음");
                            return;
                        }

                        try {
                            FaceShape faceShape = FaceShape.valueOf(faceShapeStr.toUpperCase());
                            userAnalysisCacheHelper.setFaceShape(userId, faceShape.name());
                            log.info("얼굴형 분석 성공: {}", faceShape.name());
                        } catch (IllegalArgumentException e) {
                            log.error("[응답 파싱 실패] 알 수 없는 얼굴형 문자열 수신: {}", faceShapeStr);
                        }
                    }, error -> {
                        // HTTP 에러 응답 (4xx, 5xx 등)
                        log.error("[응답 에러] 얼굴형 분석 요청 실패: {}", error.getMessage(), error);
                    });

        } catch (Exception e) {
            // MultipartBodyBuilder 구성 실패 등 코드 자체 예외
            log.error("[코드 오류] 얼굴형 분석 요청 구성 중 예외 발생: {}", e.getMessage(), e);
        }
    }




    @Async
    public void processBodyAnalysisAsync(Long userId, MultipartFile bodyImage) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("bodyImageUrl", bodyImage.getResource())
                    .filename(bodyImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            webClient.post()
                    .uri("/body-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(bodyShapeStr -> {
                        try {
                            BodyAnalysis bodyAnalysis = BodyAnalysis.valueOf(bodyShapeStr.toUpperCase());
                            userAnalysisCacheHelper.setBodyAnalysis(userId, bodyAnalysis.name());
                        } catch (IllegalArgumentException e) {
                            log.error("알 수 없는 체형 문자열 수신: {}", bodyShapeStr);
                        }

                    }, error -> {
                        log.error("AI 서버 체형 분석 실패: {}", error.getMessage(), error);
                    });

        } catch (Exception e) {
            log.error("Body analysis 요청 자체 실패: {}", e.getMessage(), e);
        }
    }


    @Transactional(readOnly = true)
    public String getFaceAnalysisResult(Long userId) {
        return userAnalysisCacheHelper.getFaceShape(userId).name();
    }

    @Transactional(readOnly = true)
    public String getBodyAnalysisResult(Long userId) {
        return userAnalysisCacheHelper.getBodyType(userId).name();
    }
}
