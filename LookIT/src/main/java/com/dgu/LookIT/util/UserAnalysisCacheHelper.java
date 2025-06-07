package com.dgu.LookIT.util;

import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.global.constant.RedisKeyConstants;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyType;
import com.dgu.LookIT.fitting.domain.FaceShape;
import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class UserAnalysisCacheHelper {

    private final RedisTemplate<String, String> redisTemplate;
    private final StyleAnalysisRepository styleAnalysisRepository;
    private static final Duration TTL = Duration.ofMinutes(10);

    public BodyType getBodyType(Long userId) {
        return BodyType.valueOf(getOrLoad(
                RedisKeyConstants.BODY_TYPE_PREFIX + userId,
                () -> styleAnalysisRepository.findByUserId(userId)
                        .map(StyleAnalysis::getBodyType)
                        .map(Enum::name)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BODY_TYPE))
        ));
    }
    public BodyAnalysis getBodyAnalysis(Long userId) {
        return BodyAnalysis.valueOf(getOrLoad(
                RedisKeyConstants.BODY_ANALYSIS_PREFIX + userId,
                () -> styleAnalysisRepository.findByUserId(userId)
                        .map(StyleAnalysis::getBodyAnalysis)
                        .map(Enum::name)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BODY_ANALYSIS))
        ));
    }

    public FaceShape getFaceShape(Long userId) {
        return FaceShape.valueOf(getOrLoad(
                RedisKeyConstants.FACE_TYPE_PREFIX + userId,
                () -> styleAnalysisRepository.findByUserId(userId)
                        .map(StyleAnalysis::getFaceShape)
                        .map(Enum::name)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FACE_ANALYSIS))
        ));
    }

    public void setBodyType(Long userId, String bodyTypeStr) {
        BodyType bodyType = parseEnum(bodyTypeStr, BodyType.class, ErrorCode.INVALID_BODY_TYPE);
        setAndPersist(
                RedisKeyConstants.BODY_TYPE_PREFIX + userId,
                bodyType.name(),
                userId,
                analysis -> analysis.setBodyType(bodyType)
        );
    }

    public void setBodyAnalysis(Long userId, String bodyAnalysisStr) {
        BodyAnalysis bodyAnalysis = parseEnum(bodyAnalysisStr, BodyAnalysis.class, ErrorCode.INVALID_BODY_TYPE);
        setAndPersist(
                RedisKeyConstants.BODY_TYPE_PREFIX + userId,
                bodyAnalysis.name(),
                userId,
                analysis -> analysis.setBodyAnalysis(bodyAnalysis)
        );
    }

    public void setFaceShape(Long userId, String faceShapeStr) {
        FaceShape faceShape = parseEnum(faceShapeStr, FaceShape.class, ErrorCode.INVALID_FACE_SHAPE);
        setAndPersist(
                RedisKeyConstants.FACE_TYPE_PREFIX + userId,
                faceShape.name(),
                userId,
                analysis -> analysis.setFaceShape(faceShape)
        );
    }

    private String getOrLoad(String key, Supplier<String> dbLoader) {
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        String value = dbLoader.get();
        redisTemplate.opsForValue().set(key, value, TTL);
        return value;
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass, ErrorCode errorCode) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommonException(errorCode);
        }
    }

    private void setAndPersist(String redisKey, String value, Long userId, java.util.function.Consumer<StyleAnalysis> updater) {
        redisTemplate.opsForValue().set(redisKey, value, TTL);

        Optional<StyleAnalysis> existing = styleAnalysisRepository.findByUserId(userId);
        StyleAnalysis analysis = existing.orElseGet(() -> new StyleAnalysis(userId));
        updater.accept(analysis);
        styleAnalysisRepository.save(analysis);
    }
}
