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

    public void requestFaceAnalysis(Long userId, MultipartFile faceImage) {
        processFaceAnalysisAsync(userId, faceImage);
    }

    public void requestBodyAnalysis(Long userId, MultipartFile bodyImage) {
        processBodyAnalysisAsync(userId, bodyImage);
    }

    @Async
    public void processFaceAnalysisAsync(Long userId, MultipartFile faceImage) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("faceImageUrl", faceImage.getResource())
                    .filename(faceImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            String faceShapeStr = webClient.post()
                    .uri("/face-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (faceShapeStr == null || faceShapeStr.isBlank()) {
                throw new CommonException(ErrorCode.AI_SERVER_ERROR);
            }

            FaceShape faceShape;
            try {
                faceShape = FaceShape.valueOf(faceShapeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommonException(ErrorCode.INVALID_FACE_SHAPE);
            }

            userAnalysisCacheHelper.setFaceShape(userId, faceShape.name());

        } catch (Exception e) {
            log.error("Face analysis failed unexpectedly: {}", e.getMessage());
        }
    }

    @Async
    public void processBodyAnalysisAsync(Long userId, MultipartFile bodyImage) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("bodyImageUrl", bodyImage.getResource())
                    .filename(bodyImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            String bodyShapeStr = webClient.post()
                    .uri("/body-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            BodyAnalysis bodyAnalysis = BodyAnalysis.valueOf(bodyShapeStr.toUpperCase());

            userAnalysisCacheHelper.setBodyAnalysis(userId, bodyAnalysis.name());
        } catch (Exception e) {
            log.error("Body analysis failed: {}", e.getMessage());
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
