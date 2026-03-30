package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DateType;
import com.nexsol.tpa.core.enums.DisplayStatus;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class InsuredContractQueryGenerator {

    public Specification<TotalFormMemberEntity> generate(InsuredSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 삭제되지 않은 데이터만 조회
            predicates.add(cb.isNull(root.get("deletedAt")));

            // 가입 상태 (DisplayStatus 기반)
            if (condition.status() != null) {
                LocalDateTime now = LocalDateTime.now();
                // 만기임박 시작일: 오늘이 만기임박 윈도우에 해당하는 보험종료일의 경계값 계산
                // → "오늘부터 만기임박인 보험종료일"의 최대값을 구하기 위해 역산
                // 만기임박 조건: 보험종료일 > today AND today >= expiringSoonStart(보험종료일)
                // DB 쿼리에서는 보험종료일의 범위로 표현해야 함

                switch (condition.status()) {
                    case DRAFT -> predicates.add(cb.equal(root.get("joinCheck"), "W"));
                    case APPLIED -> {
                        predicates.add(cb.equal(root.get("joinCheck"), "N"));
                        predicates.add(cb.equal(root.get("payYn"), "N"));
                    }
                    case JOINED -> {
                        // joinCheck = Y AND 만료되지 않았고 만기임박도 아닌 것
                        LocalDate today = LocalDate.now();
                        LocalDateTime maxExpiringSoonEnd = today.plusDays(30).atTime(23, 59, 59);
                        predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                        // insuranceEndDate > today (만료되지 않은 것)
                        predicates.add(cb.greaterThan(root.get("insuranceEndDate"), now));
                        // 만기임박 윈도우 밖 (insuranceEndDate is null OR endDate > maxExpiringSoonEnd)
                        predicates.add(cb.or(cb.isNull(root.get("insuranceEndDate")),
                                cb.greaterThan(root.get("insuranceEndDate"), maxExpiringSoonEnd)));
                    }
                    case EXPIRING_SOON -> {
                        // joinCheck = Y AND insuranceEndDate > today AND 만기임박 윈도우 내
                        LocalDate today = LocalDate.now();
                        LocalDateTime maxExpiringSoonEnd = today.plusDays(30).atTime(23, 59, 59);
                        predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                        predicates.add(cb.greaterThan(root.get("insuranceEndDate"), now));
                        predicates.add(cb.lessThanOrEqualTo(root.get("insuranceEndDate"), maxExpiringSoonEnd));
                    }
                    case EXPIRED -> {
                        // joinCheck = Y AND insuranceEndDate <= now (보험기간 만료)
                        predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                        predicates.add(cb.lessThanOrEqualTo(root.get("insuranceEndDate"), now));
                    }
                    case CANCELLED -> predicates.add(cb.equal(root.get("joinCheck"), "C"));
                    case FAILED -> {
                        predicates.add(cb.equal(root.get("joinCheck"), "F"));
                        predicates.add(cb.equal(root.get("payYn"), "N"));
                    }
                }
            }

            // 제휴사
            if (StringUtils.hasText(condition.path())) {
                predicates.add(cb.equal(root.get("path"), condition.path()));
            }
            // 채널
            if (StringUtils.hasText(condition.account())) {
                predicates.add(cb.equal(root.get("account"), condition.account()));
            }

            // 보험사
            if (StringUtils.hasText(condition.insuranceCompany())) {
                predicates.add(cb.equal(root.get("insuranceCompany"), condition.insuranceCompany()));
            }

            // 결제 여부
            if (StringUtils.hasText(condition.payYn())) {
                predicates.add(cb.equal(root.get("payYn"), condition.payYn()));
            }

            // 조회 기간 (dateType에 따라 대상 컬럼 분기)
            String dateField = switch (condition.dateType() != null ? condition.dateType() : DateType.CREATED_AT) {
                case INSURANCE_START -> "insuranceStartDate";
                case INSURANCE_END -> "insuranceEndDate";
                case CREATED_AT -> "createdAt";
            };
            if (condition.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(dateField), condition.startDate().atStartOfDay()));
            }
            if (condition.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(dateField), condition.endDate().atTime(23, 59, 59)));
            }

            // 키워드 검색
            if (StringUtils.hasText(condition.keyword())) {
                String likePattern = "%" + condition.keyword() + "%";
                predicates.add(cb.or(cb.like(root.get("businessNumber"), likePattern),
                        cb.like(root.get("phoneNumber"), likePattern), cb.like(root.get("companyName"), likePattern)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

    public Specification<TotalFormMemberEntity> generateRenewalTargetSpec(InsuredSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 삭제되지 않은 데이터만 조회
            predicates.add(cb.isNull(root.get("deletedAt")));

            // 갱신 대상: EXPIRED(기간만료) OR EXPIRING_SOON(만기임박)
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();
            LocalDateTime maxExpiringSoonEnd = today.plusDays(30).atTime(23, 59, 59);

            Predicate expired = cb.and(cb.equal(root.get("joinCheck"), "Y"),
                    cb.lessThanOrEqualTo(root.get("insuranceEndDate"), now));
            Predicate expiringSoon = cb.and(cb.equal(root.get("joinCheck"), "Y"),
                    cb.greaterThan(root.get("insuranceEndDate"), now),
                    cb.lessThanOrEqualTo(root.get("insuranceEndDate"), maxExpiringSoonEnd));

            predicates.add(cb.or(expired, expiringSoon));

            // 제휴사
            if (StringUtils.hasText(condition.path())) {
                predicates.add(cb.equal(root.get("path"), condition.path()));
            }
            // 채널
            if (StringUtils.hasText(condition.account())) {
                predicates.add(cb.equal(root.get("account"), condition.account()));
            }

            // 보험사
            if (StringUtils.hasText(condition.insuranceCompany())) {
                predicates.add(cb.equal(root.get("insuranceCompany"), condition.insuranceCompany()));
            }

            // 결제 여부
            if (StringUtils.hasText(condition.payYn())) {
                predicates.add(cb.equal(root.get("payYn"), condition.payYn()));
            }

            // 조회 기간
            String dateField = switch (condition.dateType() != null ? condition.dateType() : DateType.CREATED_AT) {
                case INSURANCE_START -> "insuranceStartDate";
                case INSURANCE_END -> "insuranceEndDate";
                case CREATED_AT -> "createdAt";
            };
            if (condition.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(dateField), condition.startDate().atStartOfDay()));
            }
            if (condition.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(dateField), condition.endDate().atTime(23, 59, 59)));
            }

            // 키워드 검색
            if (StringUtils.hasText(condition.keyword())) {
                String likePattern = "%" + condition.keyword() + "%";
                predicates.add(cb.or(cb.like(root.get("businessNumber"), likePattern),
                        cb.like(root.get("phoneNumber"), likePattern), cb.like(root.get("companyName"), likePattern)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}