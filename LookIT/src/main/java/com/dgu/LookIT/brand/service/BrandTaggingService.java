package com.dgu.LookIT.brand.service;

import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.domain.BrandTag;
import com.dgu.LookIT.brand.domain.Tag;
import com.dgu.LookIT.brand.repository.BrandRepository;
import com.dgu.LookIT.brand.repository.BrandTagRepository;
import com.dgu.LookIT.brand.repository.TagRepository;
import com.dgu.LookIT.brand.util.StyleToBrandsMap;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandTaggingService {

    private final TagRepository tagRepository;
    private final BrandTagRepository brandTagRepository;
    private final BrandRepository brandRepository;

    public void addStyleTagsToAllMappedBrands(List<StyleRecommendationResponse> recommendations) {
        List<String> lookNames = recommendations.stream()
                .skip(1) // 체형 설명 제외
                .map(StyleRecommendationResponse::lookName)
                .collect(Collectors.toList());

        for (String lookName : lookNames) {
            // 1. Tag 생성 또는 조회
            Tag tag = tagRepository.findByName(lookName)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(lookName).build()));

            // 2. 스타일 이름에 해당하는 브랜드들 가져오기
            List<StyleToBrandsMap.BrandInfo> brandInfos = StyleToBrandsMap.STYLE_BRAND_MAP.getOrDefault(lookName, List.of());

            for (StyleToBrandsMap.BrandInfo info : brandInfos) {
                // 3. 브랜드 찾거나 새로 생성
                Brand brand = brandRepository.findByName(info.getName())
                        .orElseGet(() -> brandRepository.save(Brand.builder()
                                .name(info.getName())
                                .url(info.getUrl())
                                .build()));

                // 4. 이미 연결된 경우 생략
                boolean exists = brandTagRepository.findByBrandAndTag(brand, tag).isPresent();
                if (!exists) {
                    BrandTag brandTag = BrandTag.builder()
                            .brand(brand)
                            .tag(tag)
                            .build();
                    brandTagRepository.save(brandTag);
                    brand.getBrandTags().add(brandTag);
                    tag.getBrandTags().add(brandTag);
                }
            }
        }
    }

}
