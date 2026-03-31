package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DateType;
import com.nexsol.tpa.core.enums.DisplayStatus;
import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.CoverageAmount;
import com.nexsol.tpa.storage.db.core.PremiumAmount;
import com.nexsol.tpa.storage.db.core.RefundPaymentEntity;
import com.nexsol.tpa.storage.db.core.RefundPaymentRepository;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final RefundPaymentRepository refundPaymentRepository;

    private final InsuredContractQueryGenerator queryGenerator;

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

        RefundInfo refundInfo = refundPaymentRepository.findByContractId(id).map(this::mapToRefundInfo).orElse(null);

        return new InsuredContractDetail(entity.getId(), entity.getReferIdx(), entity.getPrctrNo(),
                DisplayStatus.resolve(entity.getJoinCheck(), entity.getPayYn(), entity.getInsuranceEndDate()),
                mapToInsuredInfo(entity), mapToContractorInfo(entity), mapToBusinessLocationInfo(entity),
                mapToInsuranceSubscriptionInfo(entity), mapToPaymentInfo(entity, refundInfo));
    }

    public long countRenewalTargets(InsuredSearchCondition condition) {
        Specification<TotalFormMemberEntity> spec = queryGenerator.generateRenewalTargetSpec(condition);
        return totalFormMemberRepository.count(spec);
    }

    public List<InsuredContractDetail> findExpiringContracts() {
        // 만기임박 대상: joinCheck='Y' + 보험종료일이 만기임박 윈도우 내
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDateTime maxEnd = today.plusDays(30).atTime(23, 59, 59);

        Specification<TotalFormMemberEntity> spec = (root, query, cb) -> cb.and(cb.isNull(root.get("deletedAt")),
                cb.equal(root.get("joinCheck"), "Y"), cb.greaterThan(root.get("insuranceEndDate"), now),
                cb.lessThanOrEqualTo(root.get("insuranceEndDate"), maxEnd));

        return totalFormMemberRepository.findAll(spec)
            .stream()
            .filter(entity -> DisplayStatus.isExpiringSoon(entity.getInsuranceEndDate()))
            .map(this::mapToDetail)
            .toList();
    }

    public List<InsuredContractDetail> findContractsByStartDate(LocalDate startDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        return totalFormMemberRepository.findAllByInsuranceStartDateBetween(start, end)
            .stream()
            .map(this::mapToDetail)
            .toList();
    }

    public List<InsuredContractDetail> findBulkNotificationTargets(InsuredSearchCondition condition,
            List<DisplayStatus> statuses) {
        List<InsuredContractDetail> results = new java.util.ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        for (DisplayStatus status : statuses) {
            Specification<TotalFormMemberEntity> spec = buildBulkSpec(status, condition);
            results.addAll(totalFormMemberRepository.findAll(spec, sort).stream().map(this::mapToDetail).toList());
        }
        return results;
    }

    public BulkNotificationPreview countByStatusForPreview(InsuredSearchCondition condition) {
        long expiredCount = totalFormMemberRepository
            .count(buildBulkSpec(DisplayStatus.EXPIRED, condition));
        InsuredSearchCondition expiringSoonCondition = InsuredSearchCondition.builder()
            .account(condition.account())
            .path(condition.path())
            .insuranceCompany(condition.insuranceCompany())
            .keyword(condition.keyword())
            .build();
        long expiringSoonCount = totalFormMemberRepository
            .count(buildBulkSpec(DisplayStatus.EXPIRING_SOON, expiringSoonCondition));
        return new BulkNotificationPreview(expiredCount, expiringSoonCount, expiredCount + expiringSoonCount);
    }

    private Specification<TotalFormMemberEntity> buildBulkSpec(DisplayStatus status,
            InsuredSearchCondition condition) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            switch (status) {
                case EXPIRED -> {
                    // 기간만료: joinCheck='Y' + insuranceEndDate <= now
                    predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                    predicates.add(cb.lessThanOrEqualTo(root.get("insuranceEndDate"),
                            LocalDateTime.now()));
                    if (condition.dateType() != null && condition.startDate() != null && condition.endDate() != null) {
                        String dateField = switch (condition.dateType()) {
                            case INSURANCE_START -> "insuranceStartDate";
                            case INSURANCE_END -> "insuranceEndDate";
                            case CREATED_AT -> "createdAt";
                        };
                        predicates.add(cb.greaterThanOrEqualTo(root.get(dateField), condition.startDate().atStartOfDay()));
                        predicates.add(cb.lessThanOrEqualTo(root.get(dateField), condition.endDate().atTime(23, 59, 59)));
                    }
                }
                case EXPIRING_SOON -> {
                    // 만기임박: joinCheck='Y' + 만기임박 윈도우
                    LocalDateTime now = LocalDateTime.now();
                    LocalDate today = LocalDate.now();
                    LocalDateTime maxExpiringSoonEnd = today.plusDays(30).atTime(23, 59, 59);
                    predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                    predicates.add(cb.greaterThan(root.get("insuranceEndDate"), now));
                    predicates.add(cb.lessThanOrEqualTo(root.get("insuranceEndDate"), maxExpiringSoonEnd));
                }
                default -> throw new IllegalArgumentException("지원하지 않는 상태: " + status);
            }

            // 검색 조건 적용 (제휴사, 채널, 보험사, 키워드)
            if (StringUtils.hasText(condition.path())) {
                predicates.add(cb.equal(root.get("path"), condition.path()));
            }
            if (StringUtils.hasText(condition.account())) {
                predicates.add(cb.equal(root.get("account"), condition.account()));
            }
            if (StringUtils.hasText(condition.insuranceCompany())) {
                predicates.add(cb.equal(root.get("insuranceCompany"), condition.insuranceCompany()));
            }
            if (StringUtils.hasText(condition.keyword())) {
                String likePattern = "%" + condition.keyword() + "%";
                predicates.add(cb.or(cb.like(root.get("businessNumber"), likePattern),
                        cb.like(root.get("phoneNumber"), likePattern), cb.like(root.get("companyName"), likePattern)));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    public List<ContractExcelData> findAll(InsuredSearchCondition condition) {
        Specification<TotalFormMemberEntity> spec = queryGenerator.generate(condition);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        return totalFormMemberRepository.findAll(spec, sort)
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
            .displayStatus(
                    DisplayStatus.resolve(entity.getJoinCheck(), entity.getPayYn(), entity.getInsuranceEndDate()))
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
            .sigunguCd(entity.getSigunguCd())
            .bjdongCd(entity.getBjdongCd())
            .bun(entity.getBun())
            .ji(entity.getJi())
            .build();
    }

    // [개념 4] 보험 가입 정보 매핑 (가입금액/보험료 전체)
    private InsuredSubscriptionInfo mapToInsuranceSubscriptionInfo(TotalFormMemberEntity entity) {
        CoverageAmount coverage = entity.getCoverage();
        PremiumAmount premium = entity.getPremium();

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
            .build();
    }

    // [개념 5] 결제 정보 매핑
    private PaymentInfo mapToPaymentInfo(TotalFormMemberEntity entity, RefundInfo refundInfo) {
        return PaymentInfo.builder()
            .payStatus(entity.getPayStatus())
            .payMethod(entity.getPayMethod())
            .payDt(entity.getPayDt())
            .applyCost(entity.getApplyCost())
            .refund(refundInfo)
            .build();
    }

    private RefundInfo mapToRefundInfo(RefundPaymentEntity entity) {
        return RefundInfo.builder()
            .refundAmount(entity.getRefundAmount())
            .refundMethod(entity.getRefundMethod())
            .refundDt(entity.getRefundDt())
            .refundReason(entity.getRefundReason())
            .build();
    }

    private InsuredContractDetail mapToDetail(TotalFormMemberEntity entity) {
        return new InsuredContractDetail(entity.getId(), entity.getReferIdx(), entity.getPrctrNo(),
                DisplayStatus.resolve(entity.getJoinCheck(), entity.getPayYn(), entity.getInsuranceEndDate()),
                mapToInsuredInfo(entity), // 피보험자 정보 매핑
                mapToContractorInfo(entity), // 계약자 정보 매핑
                mapToBusinessLocationInfo(entity), // 사업장 정보 매핑
                mapToInsuranceSubscriptionInfo(entity), // 보험 가입 정보 매핑
                mapToPaymentInfo(entity, null) // 결제 정보 매핑 (환불 정보는 상세 조회에서만)
        );
    }

    private <T> Long getSafeAmount(T source, Function<T, Long> extractor) {
        if (source == null)
            return 0L;
        Long val = extractor.apply(source);
        return val == null ? 0L : val;
    }

}
