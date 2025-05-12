package com.dgu.LookIT.brand.service;

import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.dto.BrandDto;
import com.dgu.LookIT.brand.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
    public Brand saveBrand(BrandDto dto) {
        Brand brand = Brand.builder()
                .name(dto.getName())
                .url(dto.getUrl())
                .build();

        return brandRepository.save(brand);


    }
}
