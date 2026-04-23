package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pungsu_renewal_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RenewalGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "renewal_key", nullable = false)
    private String renewalKey;

    @Column(name = "contract_id", nullable = false)
    private Integer contractId;

    @Column(name = "refer_idx", nullable = false)
    private String referIdx;

    @Column(name = "renew_seq", nullable = false)
    private Integer renewSeq;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public RenewalGroupEntity(String renewalKey, Integer contractId, String referIdx, Integer renewSeq) {
        this.renewalKey = renewalKey;
        this.contractId = contractId;
        this.referIdx = referIdx;
        this.renewSeq = renewSeq;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
