package com.dgu.LookIT.brand.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.brand.dto.responseDto.BrandResponse;
import com.dgu.LookIT.brand.dto.responseDto.BrandStyleResponse;
import com.dgu.LookIT.brand.service.BrandService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/brands")
public class BrandController {
    private final BrandService brandService;

    @GetMapping("/all")
    public ResponseDto<List<BrandStyleResponse>> getAllBrands() {
        return ResponseDto.ok(brandService.getAllBrandsWithStyleNames());
    }

    // ✅ 사용자 ID로 브랜드 + 태그 조회
    @GetMapping("/recommendation")
    public ResponseDto<?> getRecommendedBrands(@UserId Long userId) {
        List<BrandResponse> recommendedBrands = brandService.getRecommendedBrands(userId);
        return ResponseDto.ok(recommendedBrands);
    }

}
