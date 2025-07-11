package com.dgu.LookIT.util;

import com.dgu.LookIT.fitting.domain.*;
import com.dgu.LookIT.global.constant.RedisKeyConstants;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import com.dgu.LookIT.user.domain.User;
import com.dgu.LookIT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class UserAnalysisCacheHelper {

    private final RedisTemplate<String, String> redisTemplate;
    private final StyleAnalysisRepository styleAnalysisRepository;
    private final UserRepository userRepository;
    private static final Duration TTL = Duration.ofMinutes(10);

    public BodyType getBodyType(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER));
        return BodyType.valueOf(getOrLoad(
                RedisKeyConstants.BODY_TYPE_PREFIX + userId,
                () -> styleAnalysisRepository.findByUser(user)
                        .map(StyleAnalysis::getBodyType)
                        .map(Enum::name)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BODY_TYPE))
        ));
    }
    public BodyAnalysis getBodyAnalysis(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER));
        return BodyAnalysis.valueOf(getOrLoad(
                RedisKeyConstants.BODY_ANALYSIS_PREFIX + userId,
                () -> styleAnalysisRepository.findByUser(user)
                        .map(StyleAnalysis::getBodyAnalysis)
                        .map(Enum::name)
                        .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BODY_ANALYSIS))
        ));
    }

    public FaceShape getFaceShape(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER));
        return FaceShape.valueOf(getOrLoad(
                RedisKeyConstants.FACE_TYPE_PREFIX + userId,
                () -> styleAnalysisRepository.findByUser(user)
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

    @Transactional
    public void setBodyAnalysis(Long userId, String bodyAnalysisStr) {
        BodyAnalysis bodyAnalysis = parseEnum(bodyAnalysisStr, BodyAnalysis.class, ErrorCode.INVALID_BODY_ANALYSIS);
        setAndPersist(
                RedisKeyConstants.BODY_TYPE_PREFIX + userId,
                bodyAnalysis.name(),
                userId,
                analysis -> analysis.setBodyAnalysis(bodyAnalysis)
        );
    }

    @Transactional
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
        User user = userRepository.findById(userId).orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER));
        redisTemplate.opsForValue().set(redisKey, value, TTL);

        Optional<StyleAnalysis> existing = styleAnalysisRepository.findByUser(user);
        StyleAnalysis analysis = existing.orElseGet(() -> new StyleAnalysis(userId));
        updater.accept(analysis);
        styleAnalysisRepository.save(analysis);
    }
}
