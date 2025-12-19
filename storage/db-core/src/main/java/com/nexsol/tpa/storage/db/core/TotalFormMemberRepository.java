package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TotalFormMemberRepository
        extends JpaRepository<TotalFormMemberEntity, Long>, JpaSpecificationExecutor<TotalFormMemberEntity> {

    // Page<TotalFormMemberEntity> findAll(Specification<TotalFormMemberEntity> spec,
    // Pageable pageable);

}
