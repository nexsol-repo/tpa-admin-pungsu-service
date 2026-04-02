package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.RenewalGroupEntity;
import com.nexsol.tpa.storage.db.core.RenewalGroupRepository;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RenewalGroupReader {

    private final RenewalGroupRepository renewalGroupRepository;

    private final TotalFormMemberRepository totalFormMemberRepository;

    public List<RenewalHistory> readByContractId(Integer contractId) {
        Optional<RenewalGroupEntity> groupEntity = renewalGroupRepository.findByContractId(contractId);

        if (groupEntity.isEmpty()) {
            return Collections.emptyList();
        }

        String renewalKey = groupEntity.get().getRenewalKey();

        List<RenewalGroupEntity> groupEntities = renewalGroupRepository
            .findAllByRenewalKeyOrderByRenewSeqDesc(renewalKey);

        return groupEntities.stream().map(entity -> {
            TotalFormMemberEntity contract = totalFormMemberRepository.findById(entity.getContractId()).orElse(null);
            return contract;
        })
            .filter(contract -> contract != null && contract.getDeletedAt() == null)
            .map(contract -> new RenewalHistory(contract.getId(), contract.getReferIdx(),
                    groupEntities.stream()
                        .filter(g -> g.getContractId().equals(contract.getId()))
                        .findFirst()
                        .map(RenewalGroupEntity::getRenewSeq)
                        .orElse(0),
                    contract.getJoinCheck(), contract.getInsuranceStartDate(), contract.getInsuranceEndDate(),
                    contract.getApplyCost(), contract.getId().equals(contractId)))
            .toList();
    }

    public Optional<String> findRenewalKeyByReferIdx(String referIdx) {
        return renewalGroupRepository.findByReferIdx(referIdx).map(RenewalGroupEntity::getRenewalKey);
    }

}
