package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
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
                .insuranceCompany(entity.getInsuranceCompany())
                .insuranceStartDate(entity.getInsuranceStartDate())
                .insuranceEndDate(entity.getInsuranceEndDate())
                .phoneNumber(entity.getPhoneNumber())
                .payYn(entity.getPayYn())
                .address(entity.getAddress())
                .joinCk(entity.getJoinCheck())
                .account(entity.getAccount())
                .path(entity.getPath())
                .build())
            .toList();

        return new DomainPage<>(contracts, result.hasNext());
    }

    public InsuredContractDetail findDetail(Integer id) {
        TotalFormMemberEntity entity = totalFormMemberRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.DEFAULT_ERROR));

        return InsuredContractDetail.builder()
            .id(entity.getId())
            .insuredInfo(mapToInsuredInfo(entity))
            .contractInfo(mapToContractInfo(entity))
            .build();
    }

    private InsuredInfo mapToInsuredInfo(TotalFormMemberEntity entity) {
        return InsuredInfo.builder()
            .companyName(entity.getCompanyName())
            .businessNumber(entity.getBusinessNumber())
            .phoneNumber(entity.getPhoneNumber())
            .address(entity.getAddress())
            .category(entity.getBizCategory())
            .structure(entity.getStructure())
            .floor(entity.getFloor())
            .prctrNo(entity.getPrctrNo())
            .pnu(entity.getPnu())
            .build();
    }

    private InsuredContractInfo mapToContractInfo(TotalFormMemberEntity entity) {
        return InsuredContractInfo.builder()
            .joinCk(entity.getJoinCheck())
            .insuranceStartDate(entity.getInsuranceStartDate())
            .insuranceEndDate(entity.getInsuranceEndDate())
            .insuranceCompany(entity.getInsuranceCompany())
            .insuranceNumber(entity.getInsuranceNumber())
            .insuranceCostBld(entity.getCoverage().getInsuranceCostBld())
            .insuranceCostFcl(entity.getCoverage().getInsuranceCostFcl())
            .insuranceCostMach(entity.getCoverage().getInsuranceCostMach())
            .insuranceCostInven(entity.getCoverage().getInsuranceCostInven())
            .insuranceCostShopSign(entity.getCoverage().getInsuranceCostShopSign())
            .insuranceCostDeductible(entity.getCoverage().getInsuranceCostDeductible())
            .totalInsuranceCost(entity.getPremium().getTotalInsuranceCost())
            .totalInsuranceMyCost(entity.getPremium().getTotalInsuranceMyCost())
            .totalGovernmentCost(entity.getPremium().getTotalGovernmentCost())
            .totalLocalGovernmentCost(entity.getPremium().getTotalLocalGovernmentCost())
            .build();
    }

}
