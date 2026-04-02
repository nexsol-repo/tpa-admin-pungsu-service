package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.RenewalGroupEntity;
import com.nexsol.tpa.storage.db.core.RenewalGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RenewalGroupWriter {

    private final RenewalGroupRepository renewalGroupRepository;

    /**
     * 최초가입: 새 renewal_key 생성 + renew_seq=0
     */
    public void createNewGroup(Integer contractId, String referIdx) {
        String renewalKey = generateRenewalKey();

        RenewalGroupEntity entity = RenewalGroupEntity.builder()
            .renewalKey(renewalKey)
            .contractId(contractId)
            .referIdx(referIdx)
            .renewSeq(0)
            .build();

        renewalGroupRepository.save(entity);
    }

    /**
     * N차 재가입: 이전 계약의 renewal_key 조회 → 동일 key + renew_seq=N
     */
    public void addToExistingGroup(String renewalKey, Integer contractId, String referIdx, int renewSeq) {
        RenewalGroupEntity entity = RenewalGroupEntity.builder()
            .renewalKey(renewalKey)
            .contractId(contractId)
            .referIdx(referIdx)
            .renewSeq(renewSeq)
            .build();

        renewalGroupRepository.save(entity);
    }

    private String generateRenewalKey() {
        return "RNW_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
