package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RenewalGroupRepository extends JpaRepository<RenewalGroupEntity, Long> {

    List<RenewalGroupEntity> findAllByRenewalKeyOrderByRenewSeqDesc(String renewalKey);

    Optional<RenewalGroupEntity> findByContractId(Integer contractId);

    Optional<RenewalGroupEntity> findByReferIdx(String referIdx);

}
