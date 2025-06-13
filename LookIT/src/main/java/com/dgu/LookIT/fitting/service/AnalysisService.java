package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.fitting.domain.FaceMood;
import com.dgu.LookIT.fitting.domain.FaceShape;
import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.fitting.dto.response.StyleAnalysisResponse;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import com.dgu.LookIT.user.domain.User;
import com.dgu.LookIT.user.repository.UserRepository;
import com.dgu.LookIT.util.UserAnalysisCacheHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
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
            // 파일 내용을 ByteArrayResource로 감싸 전송
            ByteArrayResource resource = new ByteArrayResource(faceImage.getBytes()) {
                @Override
                public String getFilename() {
                    return faceImage.getOriginalFilename();
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource) // key 이름 "file"
                    .filename(faceImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            webClient.post()
                    .uri("/face-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> {
                        log.error("[요청 실패] 얼굴형 분석 AI 서버 연결 실패: {}", e.getMessage(), e);
                    })
                    .subscribe(faceShapeStr -> {
                        if (faceShapeStr == null || faceShapeStr.isBlank()) {
                            log.error("[응답 실패] AI 서버 응답이 비어 있음");
                            return;
                        }

                        try {
                            // JSON 파싱해서 predicted_label 추출
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode node = objectMapper.readTree(faceShapeStr);
                            String label = node.get("predicted_label").asText().toUpperCase();

                            FaceShape faceShape = FaceShape.valueOf(label);
                            userAnalysisCacheHelper.setFaceShape(userId, faceShape.name());
                            log.info("얼굴형 분석 성공: {}", faceShape.name());

                        } catch (Exception e) {
                            log.error("[응답 파싱 실패] 얼굴형 JSON 파싱 실패: {}", faceShapeStr, e);
                        }
                    }, error -> {
                        log.error("[응답 에러] 얼굴형 분석 요청 실패: {}", error.getMessage(), error);
                    });

        } catch (Exception e) {
            log.error("[코드 오류] 얼굴형 분석 multipart 구성 중 예외 발생: {}", e.getMessage(), e);
        }
    }



    @Async
    public void processBodyAnalysisAsync(Long userId, MultipartFile bodyImage) {
        try {
            // 메모리에서 안전하게 전송할 수 있도록 변환
            ByteArrayResource resource = new ByteArrayResource(bodyImage.getBytes()) {
                @Override
                public String getFilename() {
                    return bodyImage.getOriginalFilename();
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource)
                    .filename(bodyImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            webClient.post()
                    .uri("/body-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(bodyShapeStr -> {
                        if (bodyShapeStr == null || bodyShapeStr.isBlank()) {
                            log.error("[응답 실패] AI 서버의 체형 응답이 비어 있음");
                            return;
                        }

                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode node = objectMapper.readTree(bodyShapeStr);
                            String label = node.get("predicted_label").asText().toUpperCase();

                            BodyAnalysis bodyAnalysis = BodyAnalysis.valueOf(label);
                            userAnalysisCacheHelper.setBodyAnalysis(userId, bodyAnalysis.name());
                            log.info("체형 분석 성공: {}", bodyAnalysis.name());

                        } catch (Exception e) {
                            log.error("[응답 파싱 실패] 알 수 없는 체형 문자열 수신 또는 JSON 파싱 실패: {}", bodyShapeStr, e);
                        }
                    }, error -> {
                        log.error("[응답 에러] AI 서버 체형 분석 실패: {}", error.getMessage(), error);
                    });

        } catch (Exception e) {
            log.error("[코드 오류] 체형 분석 요청 multipart 구성 중 예외 발생: {}", e.getMessage(), e);
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

    @Transactional(readOnly = true)
    public StyleAnalysisResponse getAnalysisResult(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        StyleAnalysis styleAnalysis = styleAnalysisRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BODY_ANALYSIS));

        return StyleAnalysisResponse.from(styleAnalysis);
    }
}
