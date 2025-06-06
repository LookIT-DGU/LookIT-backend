package com.dgu.LookIT.user.repository;

import com.dgu.LookIT.fitting.domain.StyleAnalysis;
import com.dgu.LookIT.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySerialId(Long SerialId);
}
