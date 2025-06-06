package com.dgu.LookIT.fitting.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import com.dgu.LookIT.fitting.service.StyleRecommendationService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v0/style")
@RequiredArgsConstructor
public class StyleRecommendationController {

    private final StyleRecommendationService styleRecommendationService;

    @GetMapping("/recommendation")
    public ResponseDto<List<StyleRecommendationResponse>> getStyleRecommendation(@UserId Long userId) {
        List<StyleRecommendationResponse> recommendations = styleRecommendationService.recommendStyles(userId);
        return ResponseDto.ok(recommendations);
    }
}