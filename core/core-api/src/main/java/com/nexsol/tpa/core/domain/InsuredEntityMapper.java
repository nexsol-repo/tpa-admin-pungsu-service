package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InsuredEntityMapper {

    public TotalFormMemberEntity toEntity(String referIdx, String entryDiv, InsuredInfo insured, ContractInfo contract,
            BusinessLocationInfo location, InsuredSubscriptionInfo subscription) {
        TotalFormMemberEntity entity = new TotalFormMemberEntity();
        entity.assignReferIdx(referIdx);
        entity.applyEntryDiv(entryDiv);
        updateEntity(entity, insured, contract, location, subscription);
        return entity;
    }

    public void updateEntity(TotalFormMemberEntity entity, InsuredInfo insured, ContractInfo contract,
            BusinessLocationInfo location, InsuredSubscriptionInfo subscription) {

        if (insured != null) {
            entity.applyInsuredBasic(insured.name(), insured.businessNumber(), insured.phoneNumber(), insured.email(),
                    insured.birthDate());
        }

        if (contract != null) {
            entity.applyContractInfo(contract.contractName(), contract.contractBusinessNumber(),
                    contract.contractAddress());
        }

        if (location != null) {
            // [수정] 상세 주소 코드 4종 추가 전달
            entity.applyLocationInfo(location.companyName(), location.zipCode(), location.address(),
                    location.category(), location.bizNoType(), location.biztype(), location.tenant(), location.pnu(),
                    location.prctrNo(), location.groundFloorCd(), location.groundFloor(), location.underGroundFloor(),
                    location.subFloor(), location.endSubFloor(), location.tmYn(), location.groundFloorYn(),
                    location.mainStrctType(), location.roofStrctType(),
                    // 추가된 필드 전달
                    location.sigunguCd(), location.bjdongCd(), location.bun(), location.ji());
        }

        if (subscription != null) {

            LocalDateTime createdAt = subscription.applicationDate() != null ? subscription.applicationDate()
                    : LocalDateTime.now();
            // 상태 및 기간
            entity.applyContractStatus(subscription.joinCheck(), createdAt, subscription.insuranceStartDate(),
                    subscription.insuranceEndDate(), subscription.insuranceNumber(), subscription.payYn(),
                    subscription.insuranceCompany());

            // 가입 금액 (Embedded)
            entity.applyCoverage(CoverageAmount.builder()
                .insuranceCostDeductible(subscription.insuranceCostDeductible())
                .insuranceCostBld(subscription.insuranceCostBld())
                .insuranceCostFcl(subscription.insuranceCostFcl())
                .insuranceCostMach(subscription.insuranceCostMach())
                .insuranceCostInven(subscription.insuranceCostInven())
                .insuranceCostShopSign(subscription.insuranceCostShopSign())
                .build());

            // 보험료 (Embedded)
            entity.applyPremium(PremiumAmount.builder()
                .totalInsuranceCost(subscription.totalInsuranceCost())
                .totalGovernmentCost(subscription.totalGovernmentCost())
                .totalLocalGovernmentCost(subscription.totalLocalGovernmentCost())
                .totalInsuranceMyCost(subscription.totalInsuranceMyCost())
                .build());

            // 채널 정보
            entity.applyChannelInfo(subscription.account(), subscription.path());
        }

    }

}