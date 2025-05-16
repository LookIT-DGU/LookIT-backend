package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.fitting.domain.FaceShape;
import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import com.dgu.LookIT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnalysisService {
    private final UserRepository userRepository;
    private final StyleAnalysisRepository styleAnalysisRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://ai-server.com") // ← 실제 AI 서버 주소로 바꾸세요
            .build();


    public String faceAnalysis(Long userId, MultipartFile faceImage) throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("faceImageUrl", faceImage.getResource())
                .filename(faceImage.getOriginalFilename())
                .contentType(MediaType.IMAGE_PNG);

        String faceShapeStr;
        try {
            faceShapeStr = webClient.post()
                    .uri("http://ai-server.com/face-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new CommonException(ErrorCode.AI_SERVER_ERROR); // AI 서버 통신 실패 시
        }

        FaceShape faceShape;
        try {
            faceShape = FaceShape.valueOf(faceShapeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_FACE_SHAPE);
        }

        StyleAnalysis analysis = styleAnalysisRepository.findByUserId(userId)
                .orElseGet(() -> {
                    if (!userRepository.existsById(userId)) {
                        throw new CommonException(ErrorCode.NOT_FOUND_USER);
                    }
                    return new StyleAnalysis(userId);
                });

        analysis.setFaceShape(faceShape);
        analysis.setAnalysisDate(LocalDateTime.now());
        styleAnalysisRepository.save(analysis);

        return faceShape.name();
    }

    public String bodyAnalysis(Long userId, MultipartFile bodyImage) throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("bodyImageUrl", bodyImage.getResource())
                .filename(bodyImage.getOriginalFilename())
                .contentType(MediaType.IMAGE_PNG);

        String bodyShapeStr;
        try {
            bodyShapeStr = webClient.post()
                    .uri("http://ai-server.com/body-analysis")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new CommonException(ErrorCode.AI_SERVER_ERROR); // AI 서버 통신 실패 시
        }

        BodyAnalysis bodyAnalysis;
        try {
            bodyAnalysis = BodyAnalysis.valueOf(bodyShapeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_BODY_TYPE);
        }

        StyleAnalysis analysis = styleAnalysisRepository.findByUserId(userId)
                .orElseGet(() -> {
                    if (!userRepository.existsById(userId)) {
                        throw new CommonException(ErrorCode.NOT_FOUND_USER);
                    }
                    return new StyleAnalysis(userId);
                });

        analysis.setBodyAnalysis(bodyAnalysis);
        analysis.setAnalysisDate(LocalDateTime.now());
        styleAnalysisRepository.save(analysis);

        return bodyAnalysis.name();
    }
}
