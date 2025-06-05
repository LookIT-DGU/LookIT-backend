package com.dgu.LookIT.brand.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.dto.BrandDto;
import com.dgu.LookIT.brand.service.BrandService;
import com.dgu.LookIT.brand.service.BrandTaggingService;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import com.dgu.LookIT.fitting.service.StyleRecommendationService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/brands")
public class BrandController {
    private final BrandService brandService;
    private final StyleRecommendationService styleRecommendationService;
    private final BrandTaggingService brandTaggingService;

    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody BrandDto dto) {
        Brand saved = brandService.saveBrand(dto);
        return ResponseEntity.ok(saved);
    }
    @GetMapping
    public ResponseEntity<List<Brand>> getBrands(@RequestParam(required = false) List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return ResponseEntity.ok(brandService.getAllBrands());
        }
        return ResponseEntity.ok(brandService.getBrandsByTags(tags));
    }

    @PostMapping("/auto-map")
    public ResponseDto<?> autoMapBrandsFromStyle(@UserId Long userId) {
        // 1. 스타일 추천 결과 조회
        List<StyleRecommendationResponse> recommendations = styleRecommendationService.recommendStyles(userId);

        // 2. 추천 결과 기반으로 태그 + 브랜드 연결
        brandTaggingService.addStyleTagsToAllMappedBrands(recommendations);

        return ResponseDto.ok("스타일 기반 브랜드-태그 매핑이 완료되었습니다.");
    }
    @GetMapping("/by-user")
    public ResponseDto<List<BrandDto>> getBrandsByUser(@UserId Long userId) {
        List<BrandDto> brandList = brandService.getMappedBrandsByUserId(userId);
        return ResponseDto.ok(brandList);
    }
}
