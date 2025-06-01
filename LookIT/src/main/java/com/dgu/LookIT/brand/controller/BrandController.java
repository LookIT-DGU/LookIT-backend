package com.dgu.LookIT.brand.controller;

import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.dto.BrandDto;
import com.dgu.LookIT.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/brands")
public class BrandController {
    private final BrandService brandService;

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
}
