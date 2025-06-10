package com.dgu.LookIT.fitting.repository;

import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StyleAnalysisRepository extends JpaRepository<StyleAnalysis, Long> {
    Optional<StyleAnalysis> findByUser(User user);
}
