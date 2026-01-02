package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ContractChangeDetector {

    public List<ChangeDetail> detect(TotalFormMemberEntity entity, InsuredInfo info, InsuredContractInfo contract) {
        List<ChangeDetail> changes = new ArrayList<>();

        if (info != null) {
            compare("상호명", entity.getCompanyName(), info.companyName(), changes);
            compare("생년월일", entity.getBirthDate(), info.birthDate(), changes);
            compare("피보험자명", entity.getName(), info.name(), changes);
            compare("사업자번호", entity.getBusinessNumber(), info.businessNumber(), changes);
            compare("연락처", entity.getPhoneNumber(), info.phoneNumber(), changes);
            compare("주소", entity.getAddress(), info.address(), changes);
            compare("업종", entity.getBizCategory(), info.category(), changes);
            compare("건물구조", entity.getStructure(), info.structure(), changes);
            compare("임차여부", entity.getTenant(), info.tenant(), changes);
            compare("지하소재여부", entity.getGroundFloorCd(), info.groundFloorCd(), changes);
            compare("건물 지상 층수 정보", entity.getGroundFloor(), info.groundFloor(), changes);
            compare("건물 지하 층수 정보", entity.getUnderGroundFloor(), info.underGroundFloor(), changes);
            compare("사업장 시작 층수", entity.getSubFloor(), info.subFloor(), changes);
            compare("사업장 끝 층수", entity.getEndSubFloor(), info.endSubFloor(), changes);

        }

        if (contract != null) {
            compare("진행상태", entity.getJoinCheck(), contract.joinCk(), changes);
            compare("보험사", entity.getInsuranceCompany(), contract.insuranceCompany(), changes);
            compare("증권번호", entity.getInsuranceNumber(), contract.insuranceNumber(), changes);
            compare("보험시작일", entity.getInsuranceStartDate(), contract.insuranceStartDate(), changes);
            compare("보험종료일", entity.getInsuranceEndDate(), contract.insuranceEndDate(), changes);

            // 가입금액 및 보험료 비교 (필요 시 추가)
            if (entity.getCoverage() != null) {
                compare("건물 가입 금액", entity.getCoverage().getInsuranceCostBld(), contract.insuranceCostBld(), changes);
                compare("시설/집기 금액", entity.getCoverage().getInsuranceCostFcl(), contract.insuranceCostFcl(), changes);
                compare("기계 가입 금액", entity.getCoverage().getInsuranceCostMach(), contract.insuranceCostMach(), changes);
                compare("재고자산 가입 금액", entity.getCoverage().getInsuranceCostInven(), contract.insuranceCostInven(),
                        changes);
                compare("야외간판 가입 금액", entity.getCoverage().getInsuranceCostShopSign(), contract.insuranceCostShopSign(),
                        changes);
                compare("자기부담금 가입 금액", entity.getCoverage().getInsuranceCostDeductible(),
                        contract.insuranceCostDeductible(), changes);
            }

            if (entity.getPremium() != null) {
                compare("총보험료", entity.getPremium().getTotalInsuranceCost(), contract.totalInsuranceCost(), changes);
                compare("본인부담 보험료", entity.getPremium().getTotalInsuranceMyCost(), contract.totalInsuranceMyCost(),
                        changes);
                compare("정부지원 보험료", entity.getPremium().getTotalGovernmentCost(), contract.totalGovernmentCost(),
                        changes);
                compare("지자체지원 보험료", entity.getPremium().getTotalLocalGovernmentCost(),
                        contract.totalLocalGovernmentCost(), changes);
            }
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