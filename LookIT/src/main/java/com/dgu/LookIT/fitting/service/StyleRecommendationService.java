package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.*;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import com.dgu.LookIT.fitting.dto.response.StyleTipsResponse;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import com.dgu.LookIT.fitting.repository.StyleRecommendationRepository;
import com.dgu.LookIT.user.domain.User;
import com.dgu.LookIT.user.repository.UserRepository;
import com.dgu.LookIT.util.StyleRecommendationUtil;
import com.dgu.LookIT.util.StyleTipsMapper;
import com.dgu.LookIT.util.UserAnalysisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StyleRecommendationService {

    private final UserAnalysisCacheHelper cacheHelper;
    private final StyleRecommendationRepository styleRecommendationRepository;
    private final StyleAnalysisRepository styleAnalysisRepository;
    private final UserRepository userRepository;

    public List<StyleRecommendationResponse> recommendStyles(Long userId) {
        BodyType bodyType = cacheHelper.getBodyType(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        StyleAnalysis styleAnalysis = styleAnalysisRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_STYLE_RECOMMENDATION));

        FaceMood faceMood = styleAnalysis.getFaceMood();

        return StyleRecommendationUtil.getRecommendation(bodyType, faceMood);
    }

    @Transactional
    public void setStyleTip(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        BodyAnalysis bodyAnalysis = cacheHelper.getBodyAnalysis(userId);
        String styleTips = StyleTipsMapper.getTipFor(bodyAnalysis.name());

        Optional<StyleRecommendation> existing = styleRecommendationRepository.findByUser(user);

        if (existing.isPresent()) {
            StyleRecommendation recommendation = existing.get();
            recommendation.setStylingTips(styleTips);
            styleRecommendationRepository.save(recommendation);
        } else {
            StyleRecommendation newRecommendation = StyleRecommendation.builder()
                    .user(user)
                    .stylingTips(styleTips)
                    .build();
            styleRecommendationRepository.save(newRecommendation);
        }
    }

    @Transactional(readOnly = true)
    public StyleTipsResponse getTipsByBodyAnalysis(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        StyleRecommendation recommendation = styleRecommendationRepository.findByUser(user)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_STYLE_RECOMMENDATION));

        // 3. DTO 형태로 응답 구성
        return new StyleTipsResponse(
                recommendation.getStylingTips(),
                recommendation.getRecommendationDate()
        );
    }

}
