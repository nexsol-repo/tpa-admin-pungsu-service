package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class InsuredContractFinder {

    private final TotalFormMemberRepository totalFormMemberRepository;

    private final InsuredContractQueryGenerator queryGenerator;

    private final EntityManager em;

    public DomainPage<InsuredContract> find(InsuredSearchCondition condition, OffsetLimit offsetLimit) {

        Specification<TotalFormMemberEntity> specification = queryGenerator.generate(condition);

        Pageable pageable = offsetLimit.toPageable();

        Page<TotalFormMemberEntity> result = totalFormMemberRepository.findAll(specification, pageable);

        List<InsuredContract> contracts = result.getContent().stream().map(this::mapToContract).toList();

        return new DomainPage<>(contracts, result.hasNext(), result.getTotalElements(), result.getTotalPages());
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

    public List<ContractExcelData> findAll(InsuredSearchCondition condition) {
        StringBuilder jpql = new StringBuilder();
        // 1. Root Entity는 반드시 JPA 엔티티인 TotalFormMemberEntity여야 함
        jpql.append("SELECT e FROM TotalFormMemberEntity e WHERE 1=1 ");

        // 시작일과 종료일 조건이 모두 있을 때만 필터링
        if (condition.startDate() != null && condition.endDate() != null) {
            // 보험 기간(시작~종료)이 검색 기간 내에 완전히 포함되는 계약을 조회
            jpql.append("AND e.applicationDate >= :startDate ");
            jpql.append("AND e.insuranceEndDate <= :endDate ");
        }

        if (StringUtils.hasText(condition.insuranceCompany())) {
            jpql.append("AND e.insuranceCompany = :insuranceCompany ");
        }

        jpql.append("ORDER BY e.id DESC");

        TypedQuery<TotalFormMemberEntity> query = em.createQuery(jpql.toString(), TotalFormMemberEntity.class);

        if (condition.startDate() != null && condition.endDate() != null) {
            query.setParameter("startDate", condition.startDate().atStartOfDay());
            query.setParameter("endDate", condition.endDate().atTime(23, 59, 59));
        }

        if (StringUtils.hasText(condition.insuranceCompany())) {
            query.setParameter("insuranceCompany", condition.insuranceCompany());
        }

        // 2. 조회된 엔티티를 상세 정보가 포함된 ExcelData 객체로 매핑
        return query.getResultList()
            .stream()
            .map(entity -> new ContractExcelData(mapToContract(entity), mapToInsuredInfo(entity),
                    mapToBusinessLocationInfo(entity), mapToInsuranceSubscriptionInfo(entity)))
            .toList();
    }

    private InsuredContract mapToContract(TotalFormMemberEntity entity) {
        return InsuredContract.builder()
            .id(entity.getId())
            .referIdx(entity.getReferIdx())
            .businessNumber(entity.getBusinessNumber())
            .companyName(entity.getCompanyName())
            .insuranceCompany(entity.getInsuranceCompany())
            .insuranceStartDate(entity.getInsuranceStartDate())
            .insuranceEndDate(entity.getInsuranceEndDate())
            .phoneNumber(entity.getPhoneNumber())
            .payYn(entity.getPayYn())
            .address(entity.getAddress())
            .joinCheck(entity.getJoinCheck())
            .account(entity.getAccount())
            .path(entity.getPath())
            .applicationDate(entity.getCreatedAt())
            .build();
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
            .zipCode(entity.getZipCode())
            .category(entity.getBizCategory())
            .bizNoType(entity.getBizNoType())
            .biztype(entity.getBiztype())
            .mainStrctType(entity.getMainStrctType())
            .roofStrctType(entity.getMainStrctType())
            .tenant(entity.getTenant())
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
            .applicationDate(entity.getCreatedAt())
            .insuranceCompany(entity.getInsuranceCompany())
            .insuranceNumber(entity.getInsuranceNumber())
            .payYn(entity.getPayYn())
            .account(entity.getAccount())
            .path(entity.getPath())
            // 가입 금액 매핑
            .insuranceCostBld(getSafeAmount(coverage, CoverageAmount::getInsuranceCostBld))
            .insuranceCostFcl(getSafeAmount(coverage, CoverageAmount::getInsuranceCostFcl))
            .insuranceCostMach(getSafeAmount(coverage, CoverageAmount::getInsuranceCostMach))
            .insuranceCostInven(getSafeAmount(coverage, CoverageAmount::getInsuranceCostInven))
            .insuranceCostShopSign(getSafeAmount(coverage, CoverageAmount::getInsuranceCostShopSign))
            .insuranceCostDeductible(getSafeAmount(coverage, CoverageAmount::getInsuranceCostDeductible))
            .totalInsuranceCost(getSafeAmount(premium, PremiumAmount::getTotalInsuranceCost))
            .totalGovernmentCost(getSafeAmount(premium, PremiumAmount::getTotalGovernmentCost))
            .totalLocalGovernmentCost(getSafeAmount(premium, PremiumAmount::getTotalLocalGovernmentCost))
            .totalInsuranceMyCost(getSafeAmount(premium, PremiumAmount::getTotalInsuranceMyCost))
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

    private <T> Long getSafeAmount(T source, Function<T, Long> extractor) {
        if (source == null)
            return 0L;
        Long val = extractor.apply(source);
        return val == null ? 0L : val;
    }

}
