package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.DisplayStatus;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class InsuredContractQueryGenerator {

    public Specification<TotalFormMemberEntity> generate(InsuredSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 가입 상태 (DisplayStatus 기반)
            if (condition.status() != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime sevenDaysLater = now.plusDays(7);

                switch (condition.status()) {
                    case JOINED -> {
                        // joinCheck = Y AND (insuranceEndDate is null OR insuranceEndDate
                        // >= 7일 후)
                        predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                        predicates.add(cb.or(cb.isNull(root.get("insuranceEndDate")),
                                cb.greaterThanOrEqualTo(root.get("insuranceEndDate"), sevenDaysLater)));
                    }
                    case EXPIRING_SOON -> {
                        // joinCheck = Y AND insuranceEndDate > now AND insuranceEndDate <
                        // 7일 후
                        predicates.add(cb.equal(root.get("joinCheck"), "Y"));
                        predicates.add(cb.greaterThan(root.get("insuranceEndDate"), now));
                        predicates.add(cb.lessThan(root.get("insuranceEndDate"), sevenDaysLater));
                    }
                    case EXPIRED -> predicates.add(cb.equal(root.get("joinCheck"), "X"));
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

            // 조회 기간 (보험 신청일 기준)
            if (condition.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), condition.startDate().atStartOfDay()));
            }
            if (condition.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), condition.endDate().atTime(23, 59, 59)));
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