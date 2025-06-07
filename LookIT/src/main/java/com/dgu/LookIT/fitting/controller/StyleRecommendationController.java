package com.dgu.LookIT.fitting.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import com.dgu.LookIT.fitting.dto.response.StyleTipsResponse;
import com.dgu.LookIT.fitting.service.StyleRecommendationService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0")
@RequiredArgsConstructor
public class StyleRecommendationController {

    private final StyleRecommendationService styleRecommendationService;

    @GetMapping("/style/recommendation")
    public ResponseDto<List<StyleRecommendationResponse>> getStyleRecommendation(@UserId Long userId) {
        List<StyleRecommendationResponse> recommendations = styleRecommendationService.recommendStyles(userId);
        return ResponseDto.ok(recommendations);
    }

    @GetMapping("/style-tips")
    public ResponseDto<?> getStyleTips(@UserId Long userId) {
        StyleTipsResponse response = styleRecommendationService.getTipsByBodyAnalysis(userId);
        return ResponseDto.ok(response);
    }

    @PostMapping("/style-tips")
    public ResponseDto<?> setStyleTip(@UserId Long userId) {
        styleRecommendationService.setStyleTip(userId);
        return ResponseDto.ok("스타일 팁이 저장되었습니다.");
    }
}