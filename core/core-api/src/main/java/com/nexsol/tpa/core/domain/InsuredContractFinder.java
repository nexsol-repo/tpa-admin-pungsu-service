package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuredContractFinder {

    private final TotalFormMemberRepository totalFormMemberRepository;

    private final InsuredContractQueryGenerator queryGenerator;

    public DomainPage<InsuredContract> find(InsuredSearchCondition condition, OffsetLimit offsetLimit) {

        Specification<TotalFormMemberEntity> specification = queryGenerator.generate(condition);

        Pageable pageable = offsetLimit.toPageable();

        Page<TotalFormMemberEntity> result = totalFormMemberRepository.findAll(specification, pageable);

        List<InsuredContract> contracts = result.getContent()
            .stream()
            .map(entity -> InsuredContract.builder()
                .id(entity.getId())
                .referIdx(entity.getReferIdx())
                .businessNumber(entity.getBusinessNumber())
                .companyName(entity.getCompanyName())
                .applicationDate(entity.getInsuranceStartDate())
                .insuranceCompany(entity.getInsuranceCompany())
                .insuranceStartDate(entity.getInsuranceStartDate())
                .insuranceEndDate(entity.getInsuranceEndDate())
                .phoneNumber(entity.getPhoneNumber())
                .payYn(entity.getPayYn())
                .address(entity.getAddress())
                .joinCheck(entity.getJoinCheck())
                .account(entity.getAccount())
                .path(entity.getPath())
                .build())
            .toList();

        return new DomainPage<>(contracts, result.hasNext(),result.getTotalPages());
    }

    public InsuredContractDetail findDetail(Integer id) {
        TotalFormMemberEntity entity = totalFormMemberRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        return new InsuredContractDetail(entity.getId(), entity.getReferIdx(), entity.getPrctrNo(),
                mapToInsuredInfo(entity), mapToContractorInfo(entity), mapToBusinessLocationInfo(entity),
                mapToInsuranceSubscriptionInfo(entity));
    }

    public List<InsuredContractDetail> findExpiringContracts(int days) {
        LocalDateTime start = LocalDate.now().plusDays(days).atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        // Specification을 활용하거나 Repository 직접 호출 (Implement -> Data Access)
        return totalFormMemberRepository.findAllByInsuranceEndDateBetween(start, end)
            .stream()
            .map(this::mapToDetail) // 기존 매핑 로직 재사용
            .toList();
    }

    // [개념 1] 피보험자 정보 매핑
    private InsuredInfo mapToInsuredInfo(TotalFormMemberEntity entity) {
        return InsuredInfo.builder()
            .name(entity.getName())
            .email(entity.getEmail())
            .birthDate(entity.getBirthDate())
            .businessNumber(entity.getBusinessNumber())
            .phoneNumber(entity.getPhoneNumber())
            .build();
    }

    // [개념 2] 계약자 정보 매핑 (피보험자와 다를 수 있는 정보들)
    private ContractInfo mapToContractorInfo(TotalFormMemberEntity entity) {
        return new ContractInfo(entity.getContractName(), entity.getContractBusinessNumber(),
                entity.getContractAddress());
    }

    // [개념 3] 사업장 정보 매핑 (상호명 및 층수/신규필드 포함)
    private BusinessLocationInfo mapToBusinessLocationInfo(TotalFormMemberEntity entity) {
        return BusinessLocationInfo.builder()
            .companyName(entity.getCompanyName())
            .address(entity.getAddress())
            .category(entity.getBizCategory())
            .tenant(entity.getTenant())
            .structure(entity.getStructure())
            .pnu(entity.getPnu())
            .prctrNo(entity.getPrctrNo())
            .groundFloorCd(entity.getGroundFloorCd())
            .groundFloor(entity.getGroundFloor())
            .underGroundFloor(entity.getUnderGroundFloor())
            .subFloor(entity.getSubFloor())
            .endSubFloor(entity.getEndSubFloor())
            .tmYn(entity.getTmYn())
            .groundFloorYn(entity.getGroundFloorYn())
            .build();
    }

    // [개념 4] 보험 가입 정보 매핑 (가입금액/보험료 전체)
    private InsuredSubscriptionInfo mapToInsuranceSubscriptionInfo(TotalFormMemberEntity entity) {
        CoverageAmount coverage = entity.getCoverage();
        PremiumAmount premium = entity.getPremium();
        LocalDateTime now = LocalDateTime.now();

        return InsuredSubscriptionInfo.builder()
            .joinCheck(entity.getJoinCheck())
            .insuranceStartDate(entity.getInsuranceStartDate())
            .insuranceEndDate(entity.getInsuranceEndDate())
            .insuranceCompany(entity.getInsuranceCompany())
            .insuranceNumber(entity.getInsuranceNumber())
            .payYn(entity.getPayYn())
            .account(entity.getAccount())
            .path(entity.getPath())
            // 가입 금액 매핑
            .insuranceCostBld(coverage != null ? coverage.getInsuranceCostBld() : 0L)
            .insuranceCostFcl(coverage != null ? coverage.getInsuranceCostFcl() : 0L)
            .insuranceCostMach(coverage != null ? coverage.getInsuranceCostMach() : 0L)
            .insuranceCostInven(coverage != null ? coverage.getInsuranceCostInven() : 0L)
            .insuranceCostShopSign(coverage != null ? coverage.getInsuranceCostShopSign() : 0L)
            .insuranceCostDeductible(coverage != null ? coverage.getInsuranceCostDeductible() : 0L)
            // 보험료 매핑
            .totalInsuranceCost(premium != null ? premium.getTotalInsuranceCost() : 0L)
            .totalGovernmentCost(premium != null ? premium.getTotalGovernmentCost() : 0L)
            .totalLocalGovernmentCost(premium != null ? premium.getTotalLocalGovernmentCost() : 0L)
            .totalInsuranceMyCost(premium != null ? premium.getTotalInsuranceMyCost() : 0L)
            .isRenewalTarget(InsuredSubscriptionInfo.calculateRenewalTarget(entity.getInsuranceEndDate(), now))
            .build();
    }

    private InsuredContractDetail mapToDetail(TotalFormMemberEntity entity) {
        return new InsuredContractDetail(entity.getId(), entity.getReferIdx(), entity.getPrctrNo(),
                mapToInsuredInfo(entity), // 피보험자 정보 매핑
                mapToContractorInfo(entity), // 계약자 정보 매핑
                mapToBusinessLocationInfo(entity), // 사업장 정보 매핑
                mapToInsuranceSubscriptionInfo(entity) // 보험 가입 정보 매핑
        );
    }

}
