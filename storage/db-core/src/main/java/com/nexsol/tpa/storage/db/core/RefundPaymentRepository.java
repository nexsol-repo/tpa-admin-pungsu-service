package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundPaymentRepository extends JpaRepository<RefundPaymentEntity, Long> {

    Optional<RefundPaymentEntity> findByContractId(Integer contractId);

}