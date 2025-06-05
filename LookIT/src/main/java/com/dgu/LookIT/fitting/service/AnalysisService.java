package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.fitting.domain.FaceShape;
import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import com.dgu.LookIT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisService {
    private final UserRepository userRepository;
    private final StyleAnalysisRepository styleAnalysisRepository;
//    private final StringRedisTemplate redisTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://ai-server.com")
            .build();

    public String requestFaceAnalysis(Long userId, MultipartFile faceImage) throws IOException {
        String taskId = UUID.randomUUID().toString();
        processFaceAnalysisAsync(taskId, userId, faceImage);
        return taskId; // 클라이언트는 taskId로 결과를 나중에 조회
    }

    public String requestBodyAnalysis(Long userId, MultipartFile bodyImage) throws IOException {
        String taskId = UUID.randomUUID().toString();
        processBodyAnalysisAsync(taskId, userId, bodyImage);
        return taskId;
    }

    @Async
    public void processFaceAnalysisAsync(String taskId, Long userId, MultipartFile faceImage) {
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

            redisTemplate.opsForValue().set("analysis:face:" + taskId, faceShape.name());

        } catch (CommonException e) {
            log.error("Face analysis failed with known error: {}", e.getErrorCode().name());
            redisTemplate.opsForValue().set("analysis:face:" + taskId, "ERROR:" + e.getErrorCode().name());
        } catch (Exception e) {
            log.error("Face analysis failed unexpectedly: {}", e.getMessage());
            redisTemplate.opsForValue().set("analysis:face:" + taskId, "ERROR:AI_SERVER_ERROR");
        }
    }


    @Async
    public void processBodyAnalysisAsync(String taskId, Long userId, MultipartFile bodyImage) {
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

            StyleAnalysis analysis = styleAnalysisRepository.findByUserId(userId)
                    .orElseGet(() -> new StyleAnalysis(userId));

            analysis.setBodyAnalysis(bodyAnalysis);
            analysis.setAnalysisDate(LocalDateTime.now());
            styleAnalysisRepository.save(analysis);

            redisTemplate.opsForValue().set("analysis:body:" + taskId, bodyAnalysis.name());
        } catch (Exception e) {
            log.error("Body analysis failed: {}", e.getMessage());
            redisTemplate.opsForValue().set("analysis:body:" + taskId, "ERROR");
        }
    }

//    public String getFaceAnalysisResult(String taskId, Long userId) {
//        ValueOperations<String, String> ops = redisTemplate.opsForValue();
//        String key = "analysis:face:" + taskId;
//        String result = ops.get(key);
//
//        if (result == null) {
//            // 1. Redis에 없을 경우 DB에서 조회
//            result = styleAnalysisRepository.findByUserId(userId)
//                    .map(StyleAnalysis::getFaceShape)
//                    .orElse(null);
//
//            // 2. 조회된 결과가 있으면 Redis에 캐싱
//            if (result != null) {
//                ops.set(key, result, Duration.ofMinutes(10)); // TTL 10분 등 설정 가능
//            }
//        }
//
//        return result;
//    }

    public String getBodyAnalysisResult(String taskId) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get("analysis:body:" + taskId);
    }
}
