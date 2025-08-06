package com.fisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisa.entity.Oehwa;
import java.util.Optional;

public interface OehwaRepository extends JpaRepository<Oehwa, Integer> {
    // guid로 Oehwa 단건 조회
    Optional<Oehwa> findByGuid(String guid);

}
