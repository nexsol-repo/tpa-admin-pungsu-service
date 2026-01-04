package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class InsuredEntityMapper {

    // 식별자와 DTO들을 받아 완성된 Entity를 반환
    public TotalFormMemberEntity toEntity(String referIdx, InsuredInfo info, InsuredContractInfo contract) {
        TotalFormMemberEntity entity = new TotalFormMemberEntity();

        // 도메인 객체에게 식별자 할당 요청
        entity.assignReferIdx(referIdx);

        if (info != null) {
            entity.applyInsuredBasic(info.companyName(), info.name(), info.businessNumber(), info.phoneNumber(),
                    info.email(), info.birthDate());

            entity.applyLocationInfo(info.address(), info.tenant(), info.category(), info.structure(), info.prctrNo(),
                    info.pnu(), info.groundFloorCd(), info.groundFloor(), info.underGroundFloor(), info.subFloor(),
                    info.endSubFloor());
        }

        if (contract != null) {
            entity.applyContractStatus(contract.joinCk(), contract.insuranceStartDate(), contract.insuranceEndDate(),
                    contract.insuranceNumber(), contract.payYn());

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

            entity.applyChannelInfo(contract.account(), contract.path());

            // 필요 시 채널 정보 매핑 추가
            entity.applyChannelInfo(contract.account(), contract.path());
        }

        return entity;
    }

}