package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RenewalGroupService {

    private final RenewalGroupReader renewalGroupReader;

    private final RenewalGroupWriter renewalGroupWriter;

    @Transactional(readOnly = true)
    public List<RenewalHistory> getRenewalHistory(Integer contractId) {
        return renewalGroupReader.readByContractId(contractId);
    }

    @Transactional
    public void registerRenewalGroup(Integer contractId, String referIdx, String bfReferIdx, int joinRenew) {
        if (joinRenew == 0 || bfReferIdx == null || bfReferIdx.isEmpty()) {
            // 최초가입
            renewalGroupWriter.createNewGroup(contractId, referIdx);
            return;
        }

        // N차 재가입: 이전 계약의 renewal_key 조회
        Optional<String> renewalKey = renewalGroupReader.findRenewalKeyByReferIdx(bfReferIdx);

        if (renewalKey.isPresent()) {
            renewalGroupWriter.addToExistingGroup(renewalKey.get(), contractId, referIdx, joinRenew);
        }
        else {
            // 이전 계약이 그룹에 없는 경우: 새 그룹 생성
            renewalGroupWriter.createNewGroup(contractId, referIdx);
        }
    }

}
