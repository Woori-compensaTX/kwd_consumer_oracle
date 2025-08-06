package com.fisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fisa.entity.UserBank;

import java.util.Optional;

public interface MisUserBankRepository extends JpaRepository<UserBank, String> {
    // userId 기준 최신 1개 거래 (MySQL용 LIMIT 1)
      Optional<UserBank> findByUserId(String userId);
}
