package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeritzSixRepository extends JpaRepository<MeritzSixEntity, Integer> {
    Optional<MeritzSixEntity> findByPrctrNoAndErrCd(String prctrNo, String errCd);

}
