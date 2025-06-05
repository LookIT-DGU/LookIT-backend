package com.dgu.LookIT.brand.repository;

import com.dgu.LookIT.brand.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByTags_NameIn(List<String> tagNames);
    Optional<Brand> findByName(String name);

    @Query("""
    SELECT DISTINCT bt.brand
    FROM BrandTag bt
    WHERE bt.tag.name IN (
        SELECT sr.styleType
        FROM StyleRecommendation sr
        WHERE sr.user.id = :userId
    )
""")
    List<Brand> findBrandsByUserId(@Param("userId") Long userId);
}