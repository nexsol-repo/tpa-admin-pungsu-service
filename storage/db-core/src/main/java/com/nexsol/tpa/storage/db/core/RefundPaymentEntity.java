package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pungsu_payment_cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Integer contractId;

    @Column(name = "refund_amount", nullable = false)
    private Long refundAmount;

    @Column(name = "refund_method")
    private String refundMethod;

    @Column(name = "refund_dt", nullable = false)
    private LocalDateTime refundDt;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public RefundPaymentEntity(Integer contractId, Long refundAmount, String refundMethod, LocalDateTime refundDt,
            String refundReason) {
        this.contractId = contractId;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundDt = refundDt;
        this.refundReason = refundReason;
    }

    public void update(Long refundAmount, String refundMethod, LocalDateTime refundDt, String refundReason) {
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundDt = refundDt;
        this.refundReason = refundReason;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}