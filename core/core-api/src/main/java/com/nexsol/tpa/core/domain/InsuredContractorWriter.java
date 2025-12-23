package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsuredContractorWriter {

    private final TotalFormMemberRepository totalFormMemberRepository;

    public Integer write(Integer id, InsuredInfo info, InsuredContractInfo contract) {
        TotalFormMemberEntity entity = totalFormMemberRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        if (info != null) {
            entity.applyInsuredBasic(info.companyName(), info.name(), info.businessNumber(), info.phoneNumber());

            entity.applyLocationInfo(info.address(), info.tenant(), info.category(), info.structure(), info.floor(),
                    info.prctrNo(), info.pnu());
        }

        if (contract != null) {
            // 보험 기본 정보 업데이트 (상태, 보험사, 기간 등 누락된 메서드 추가 필요 시)
            entity.applyContractStatus(contract.joinCk(), contract.insuranceStartDate(), contract.insuranceEndDate(),
                    contract.insuranceNumber());

            entity.applyCoverage(CoverageAmount.builder()
                .insuranceCostDeductible(contract.insuranceCostDeductible())
                .insuranceCostBld(contract.insuranceCostBld())
                .insuranceCostFcl(contract.insuranceCostFcl())
                .insuranceCostMach(contract.insuranceCostMach())
                .insuranceCostInven(contract.insuranceCostInven())
                .insuranceCostShopSign(contract.insuranceCostShopSign())
                .build());

            entity.applyPremium(PremiumAmount.builder()
                .totalInsuranceCost(contract.totalInsuranceCost())
                .totalGovernmentCost(contract.totalGovernmentCost())
                .totalLocalGovernmentCost(contract.totalLocalGovernmentCost())
                .totalInsuranceMyCost(contract.totalInsuranceMyCost())
                .build());
        }
        return entity.getId();
    }

}
