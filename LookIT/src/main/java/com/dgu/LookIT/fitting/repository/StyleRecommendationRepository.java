package com.dgu.LookIT.fitting.repository;

import com.dgu.LookIT.fitting.domain.StyleRecommendation;
import com.dgu.LookIT.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StyleRecommendationRepository extends JpaRepository<StyleRecommendation, Long> {
    Optional<StyleRecommendation> findByUser(User user);
}
