package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class InsuredContractQueryGenerator {

    public Specification<TotalFormMemberEntity> generate(InsuredSearchCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 상태
            if (StringUtils.hasText(condition.status())) {
                predicates.add(cb.equal(root.get("joinCheck"), condition.status()));

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
                predicates
                    .add(cb.greaterThanOrEqualTo(root.get("applicationDate"), condition.startDate().atStartOfDay()));
            }
            if (condition.endDate() != null) {
                predicates
                    .add(cb.lessThanOrEqualTo(root.get("insuranceEndDate"), condition.endDate().atTime(23, 59, 59)));
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
