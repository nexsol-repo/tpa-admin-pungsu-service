package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

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
                .businessNumber(entity.getBusinessNumber())
                .companyName(entity.getCompanyName())
                .applicationDate(entity.getInsuranceStartDate())
                .insuranceStartDate(entity.getInsuranceStartDate())
                .insuranceEndDate(entity.getInsuranceEndDate())
                .phoneNumber(entity.getPhoneNumber())
                .payMethod(entity.getPayMethod())
                .address(entity.getAddress())
                .build())
            .toList();

        return new DomainPage<>(contracts, result.hasNext());
    }

}
