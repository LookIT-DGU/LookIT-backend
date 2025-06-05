package com.dgu.LookIT.brand.service;

import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.domain.BrandTag;
import com.dgu.LookIT.brand.domain.Tag;
import com.dgu.LookIT.brand.dto.BrandDto;
import com.dgu.LookIT.brand.repository.BrandRepository;
import com.dgu.LookIT.brand.repository.TagRepository;
import com.dgu.LookIT.brand.repository.BrandTagRepository;
import com.dgu.LookIT.fitting.domain.StyleRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final BrandTagRepository brandTagRepository;

    public Brand saveBrand(BrandDto dto) {
        // 1. Brand 객체 생성 및 저장
        Brand brand = Brand.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .build();
        brand = brandRepository.save(brand);

        // 2. Tag 이름 목록 처리 → 존재하지 않으면 생성
        Set<String> tagNames = dto.getStyleTags() != null ? new HashSet<>(dto.getStyleTags()) : new HashSet<>();

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

            // 3. BrandTag 객체 생성 후 저장
            BrandTag brandTag = BrandTag.builder()
                    .brand(brand)
                    .tag(tag)
                    .build();
            brandTagRepository.save(brandTag);

            // 4. 양방향 설정 (선택)
            brand.getBrandTags().add(brandTag);
            tag.getBrandTags().add(brandTag);
        }

        return brand;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> getBrandsByTags(List<String> tagNames) {
        return brandRepository.findAll().stream()
                .filter(brand -> brand.getBrandTags().stream()
                        .map(brandTag -> brandTag.getTag().getName())
                        .anyMatch(tagNames::contains))
                .collect(Collectors.toList());
    }
    public List<BrandDto> getMappedBrandsByUserId(Long userId) {
        List<StyleRecommendation> recommendations = Brand.findByUserId(userId);

        return recommendations.stream()
                .map(StyleRecommendation::)
                .distinct()
                .flatMap(styleType ->
                        StyleToBrandsMap.STYLE_BRAND_MAP.getOrDefault(styleType, List.of()).stream()
                )
                .distinct() // 중복 브랜드 제거
                .map(info -> new BrandDto(info.getName(), info.getUrl()))
                .toList();
    }

}
