package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ContractChangeDetector {

    public List<ChangeDetail> detect(TotalFormMemberEntity entity, InsuredInfo insured, ContractInfo contract,
            BusinessLocationInfo location, InsuredSubscriptionInfo subscription) {
        List<ChangeDetail> changes = new ArrayList<>();

        if (insured != null) {
            compare("피보험자명", entity.getName(), insured.name(), changes);
            compare("사업자번호", entity.getBusinessNumber(), insured.businessNumber(), changes);
            compare("연락처", entity.getPhoneNumber(), insured.phoneNumber(), changes);
            compare("이메일", entity.getEmail(), insured.email(), changes);
            compare("생년월일", entity.getBirthDate(), insured.birthDate(), changes);
        }

        if (contract != null) {
            compare("계약자명", entity.getContractName(), contract.contractName(), changes);
            compare("계약자 사업자번호", entity.getContractBusinessNumber(), contract.contractBusinessNumber(), changes);
            compare("계약자 주소", entity.getContractAddress(), contract.contractAddress(), changes);
        }

        if (location != null) {
            compare("상호명(사업장명)", entity.getCompanyName(), location.companyName(), changes);
            compare("사업장 주소", entity.getAddress(), location.address(), changes);
            compare("업종", entity.getBizCategory(), location.category(), changes);
            compare("임차여부", entity.getTenant(), location.tenant(), changes);
            compare("건물구조", entity.getStructure(), location.structure(), changes);
            compare("지하소재여부", entity.getGroundFloorCd(), location.groundFloorCd(), changes);
            compare("전통시장 여부", entity.getTmYn(), location.tmYn(), changes);
            compare("지하층/1층 여부", entity.getGroundFloorYn(), location.groundFloorYn(), changes);
            compare("지상층수", entity.getGroundFloor(), location.groundFloor(), changes);
            compare("지하층수", entity.getUnderGroundFloor(), location.underGroundFloor(), changes);
            compare("시작호수", entity.getSubFloor(), location.subFloor(), changes);
            compare("종료호수", entity.getEndSubFloor(), location.endSubFloor(), changes);
            compare("전통시장 여부", entity.getTmYn(), location.tmYn(), changes);
        }
        if (subscription != null) {
            CoverageAmount coverage = entity.getCoverage();
            compare("진행상태", entity.getJoinCheck(), subscription.joinCheck(), changes);
            compare("보험사", entity.getInsuranceCompany(), subscription.insuranceCompany(), changes);
            compare("보험시작일", entity.getInsuranceStartDate(), subscription.insuranceStartDate(), changes);
            compare("보험종료일", entity.getInsuranceEndDate(), subscription.insuranceEndDate(), changes);

            if (coverage != null) {
                compare("건물 가입금액", coverage.getInsuranceCostBld(), subscription.insuranceCostBld(), changes);
                compare("시설 가입금액", coverage.getInsuranceCostFcl(), subscription.insuranceCostFcl(), changes);
                compare("기계 가입금액", coverage.getInsuranceCostMach(), subscription.insuranceCostMach(), changes);
                compare("재고 가입금액", coverage.getInsuranceCostInven(), subscription.insuranceCostInven(), changes);
                compare("간판 가입금액", coverage.getInsuranceCostShopSign(), subscription.insuranceCostShopSign(), changes);
                compare("자기부담금", coverage.getInsuranceCostDeductible(), subscription.insuranceCostDeductible(),
                        changes);
            }

            PremiumAmount premium = entity.getPremium();
            if (premium != null) {
                compare("총 보험료", premium.getTotalInsuranceCost(), subscription.totalInsuranceCost(), changes);
                compare("정부지원금", premium.getTotalGovernmentCost(), subscription.totalGovernmentCost(), changes);
                compare("지자체지원금", premium.getTotalLocalGovernmentCost(), subscription.totalLocalGovernmentCost(),
                        changes);
                compare("자부담 보험료", premium.getTotalInsuranceMyCost(), subscription.totalInsuranceMyCost(), changes);
            }

            compare("진행상태", entity.getJoinCheck(), subscription.joinCheck(), changes);
            compare("결제여부", entity.getPayYn(), subscription.payYn(), changes);
        }

        return changes;
    }

    private void compare(String label, Object oldVal, Object newVal, List<ChangeDetail> changes) {
        // null-safe 비교: 값이 존재하고 서로 다를 때만 기록
        if (newVal != null && !Objects.equals(oldVal, newVal)) {
            changes.add(new ChangeDetail(label, String.valueOf(oldVal), String.valueOf(newVal)));
        }
    }

}