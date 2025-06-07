package com.dgu.LookIT.brand.repository;

import com.dgu.LookIT.brand.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("SELECT DISTINCT b FROM Brand b " +
            "LEFT JOIN FETCH b.brandTags bt " +
            "LEFT JOIN FETCH bt.tag " +
            "WHERE b.user.id = :userId")
    List<Brand> findBrandsWithTagsByUserId(@Param("userId") Long userId);


    List<Brand> findByBrandTags_Tag_NameIn(List<String> tagNames);
}