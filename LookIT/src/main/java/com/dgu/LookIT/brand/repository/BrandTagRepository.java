package com.dgu.LookIT.brand.repository;

import com.dgu.LookIT.brand.domain.BrandTag;
import com.dgu.LookIT.brand.domain.Brand;
import com.dgu.LookIT.brand.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandTagRepository extends JpaRepository<BrandTag, Long> {

    // 특정 브랜드에 연결된 모든 BrandTag
    List<BrandTag> findByBrand(Brand brand);

    // 특정 태그에 연결된 모든 BrandTag
    List<BrandTag> findByTag(Tag tag);

    // 브랜드와 태그로 BrandTag 단건 조회
    Optional<BrandTag> findByBrandAndTag(Brand brand, Tag tag);

    // 브랜드 ID로 전체 조회
    List<BrandTag> findByBrandId(Long brandId);

    // 태그 ID로 전체 조회
    List<BrandTag> findByTagId(Long tagId);
}
