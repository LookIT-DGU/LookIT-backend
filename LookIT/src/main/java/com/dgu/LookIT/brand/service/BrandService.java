package com.dgu.LookIT.brand.service;

import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.dto.responseDto.BrandResponse;
import com.dgu.LookIT.brand.dto.responseDto.BrandStyleResponse;
import com.dgu.LookIT.brand.repository.BrandRepository;
import com.dgu.LookIT.brand.util.StyleToBrandsMap;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import com.dgu.LookIT.fitting.service.StyleRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final StyleRecommendationService styleRecommendationService;

    public List<BrandResponse> getRecommendedBrands(Long userId) {
        List<StyleRecommendationResponse> styleList = styleRecommendationService.recommendStyles(userId);

        // 3. 스타일 이름(lookName)을 통해 STYLE_BRAND_MAP에서 브랜드 리스트 조회
        return styleList.stream()
                .map(StyleRecommendationResponse::lookName) // ✅ record method
                .flatMap(styleName -> StyleToBrandsMap.STYLE_BRAND_MAP
                        .getOrDefault(styleName, List.of()).stream())
                .distinct() // 중복 브랜드 제거
                .map(info -> new BrandResponse(info.getName(), info.getUrl()))
                .toList();
    }

    public List<BrandStyleResponse> getAllBrandsWithStyleNames() {
        return StyleToBrandsMap.STYLE_BRAND_MAP.entrySet().stream()
                .flatMap(entry -> {
                    String styleName = entry.getKey();
                    return entry.getValue().stream()
                            .map(brandInfo -> new BrandStyleResponse(brandInfo.getName(), brandInfo.getUrl(), styleName));
                })
                .toList();
    }


//    public List<Brand> getBrandsByTags(List<String> tagNames) {
//        return brandRepository.findAll().stream()
//                .filter(brand -> brand.getBrandTags().stream()
//                        .map(brandTag -> brandTag.getTag().getName())
//                        .anyMatch(tagNames::contains))
//                .collect(Collectors.toList());
//    }

}
