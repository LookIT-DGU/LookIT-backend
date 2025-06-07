package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.fitting.domain.FaceShape;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisService {

    private final UserAnalysisCacheHelper userAnalysisCacheHelper;
    private final WebClient webClient;

    public void postBodyType(Long userId, String bodyAnalysis) {
        userAnalysisCacheHelper.setBodyType(userId, bodyAnalysis);
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

    public String getFaceAnalysisResult(Long userId) {
        return userAnalysisCacheHelper.getFaceShape(userId).name();
    }

    public String getBodyAnalysisResult(Long userId) {
        return userAnalysisCacheHelper.getBodyType(userId).name();
    }
}
