package com.dgu.LookIT.brand.service;

import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.domain.Tag;
import com.dgu.LookIT.brand.dto.BrandDto;
import com.dgu.LookIT.brand.repository.BrandRepository;
import com.dgu.LookIT.brand.repository.TagRepository;
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

    public Brand saveBrand(BrandDto dto) {
        Set<Tag> tags = new HashSet<>();
        if (dto.getStyleTags() != null) {
            tags = dto.getStyleTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build())))
                    .collect(Collectors.toSet());
        }

        Brand brand = Brand.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .tags(tags)
                .build();

        return brandRepository.save(brand);
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> getBrandsByTags(List<String> tags) {
        return brandRepository.findAll().stream()
                .filter(b -> b.getTags().stream().anyMatch(t -> tags.contains(t.getName())))
                .collect(Collectors.toList());
    }
}
