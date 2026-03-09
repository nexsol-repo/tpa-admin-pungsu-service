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

    public List<String> detect(TotalFormMemberEntity entity, InsuredInfo insured, ContractInfo contract,
            BusinessLocationInfo location, InsuredSubscriptionInfo subscription, PaymentInfo payment) {
        List<String> changedSections = new ArrayList<>();

        if (hasApplicantChanged(entity, insured, contract, location)) {
            changedSections.add("가입자 정보");
        }

        if (hasInsuranceChanged(entity, subscription)) {
            changedSections.add("보험 가입 정보");
        }

        if (hasPaymentChanged(entity, payment)) {
            changedSections.add("결제 정보");
        }

        return changedSections;
    }

    private boolean hasApplicantChanged(TotalFormMemberEntity entity, InsuredInfo insured, ContractInfo contract,
            BusinessLocationInfo location) {
        if (insured != null) {
            if (changed(entity.getName(), insured.name())
                    || changed(entity.getBusinessNumber(), insured.businessNumber())
                    || changed(entity.getPhoneNumber(), insured.phoneNumber())
                    || changed(entity.getEmail(), insured.email())
                    || changed(entity.getBirthDate(), insured.birthDate())) {
                return true;
            }
        }

        if (contract != null) {
            if (changed(entity.getContractName(), contract.contractName())
                    || changed(entity.getContractBusinessNumber(), contract.contractBusinessNumber())
                    || changed(entity.getContractAddress(), contract.contractAddress())) {
                return true;
            }
        }

        if (location != null) {
            if (changed(entity.getCompanyName(), location.companyName())
                    || changed(entity.getAddress(), location.address())
                    || changed(entity.getBizCategory(), location.category())
                    || changed(entity.getTenant(), location.tenant())
                    || changed(entity.getMainStrctType(), location.mainStrctType())
                    || changed(entity.getRoofStrctType(), location.roofStrctType())
                    || changed(entity.getGroundFloorCd(), location.groundFloorCd())
                    || changed(entity.getTmYn(), location.tmYn())
                    || changed(entity.getGroundFloorYn(), location.groundFloorYn())
                    || changed(entity.getGroundFloor(), location.groundFloor())
                    || changed(entity.getUnderGroundFloor(), location.underGroundFloor())
                    || changed(entity.getSubFloor(), location.subFloor())
                    || changed(entity.getEndSubFloor(), location.endSubFloor())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasInsuranceChanged(TotalFormMemberEntity entity, InsuredSubscriptionInfo subscription) {
        if (subscription == null) {
            return false;
        }

        if (changed(entity.getJoinCheck(), subscription.joinCheck())
                || changed(entity.getInsuranceCompany(), subscription.insuranceCompany())
                || changed(entity.getInsuranceStartDate(), subscription.insuranceStartDate())
                || changed(entity.getInsuranceEndDate(), subscription.insuranceEndDate())
                || changed(entity.getPayYn(), subscription.payYn())) {
            return true;
        }

        CoverageAmount coverage = entity.getCoverage();
        if (coverage != null) {
            if (changed(coverage.getInsuranceCostBld(), subscription.insuranceCostBld())
                    || changed(coverage.getInsuranceCostFcl(), subscription.insuranceCostFcl())
                    || changed(coverage.getInsuranceCostMach(), subscription.insuranceCostMach())
                    || changed(coverage.getInsuranceCostInven(), subscription.insuranceCostInven())
                    || changed(coverage.getInsuranceCostShopSign(), subscription.insuranceCostShopSign())
                    || changed(coverage.getInsuranceCostDeductible(), subscription.insuranceCostDeductible())) {
                return true;
            }
        }

        PremiumAmount premium = entity.getPremium();
        if (premium != null) {
            if (changed(premium.getTotalInsuranceCost(), subscription.totalInsuranceCost())
                    || changed(premium.getTotalGovernmentCost(), subscription.totalGovernmentCost())
                    || changed(premium.getTotalLocalGovernmentCost(), subscription.totalLocalGovernmentCost())
                    || changed(premium.getTotalInsuranceMyCost(), subscription.totalInsuranceMyCost())) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPaymentChanged(TotalFormMemberEntity entity, PaymentInfo payment) {
        if (payment == null) {
            return false;
        }

        return changed(entity.getPayStatus(), payment.payStatus())
                || changed(entity.getPayMethod(), payment.payMethod())
                || changed(entity.getPayDt(), payment.payDt())
                || changed(entity.getApplyCost(), payment.applyCost());
    }

    private boolean changed(Object oldVal, Object newVal) {
        return newVal != null && !Objects.equals(oldVal, newVal);
    }

}