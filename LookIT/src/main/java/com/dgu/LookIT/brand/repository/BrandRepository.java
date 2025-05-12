package com.dgu.LookIT.brand.repository;

import com.dgu.LookIT.brand.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    //쿼리메소드정의
}