package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface TotalFormMemberRepository
        extends JpaRepository<TotalFormMemberEntity, Integer>, JpaSpecificationExecutor<TotalFormMemberEntity> {

    List<TotalFormMemberEntity> findAllByInsuranceEndDateBetween(LocalDateTime start, LocalDateTime end);
    // Page<TotalFormMemberEntity> findAll(Specification<TotalFormMemberEntity> spec,
    // Pageable pageable);

}
