package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuredContractorWriter {

    private final TotalFormMemberRepository totalFormMemberRepository;

    private final ContractKeyGenerator keyGenerator;

    private final InsuredEntityMapper entityMapper;

    private final ContractChangeDetector changeDetector;

    public Integer write(Integer id, InsuredInfo insured, ContractInfo contract, BusinessLocationInfo location,
            InsuredSubscriptionInfo subscription) {
        TotalFormMemberEntity entity = totalFormMemberRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        // 매핑 책임은 Mapper에게 격벽으로 분리
        entityMapper.updateEntity(entity, insured, contract, location, subscription);

        return entity.getId();
    }

    public List<ChangeDetail> writeAndGetDiff(Integer id, InsuredInfo insured, ContractInfo contract,
            BusinessLocationInfo location, InsuredSubscriptionInfo subscription) {
        TotalFormMemberEntity entity = totalFormMemberRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));

        // 1. 수정 전 상태와 새로운 개념 객체들을 비교하여 변경 사항 추출
        List<ChangeDetail> diffs = changeDetector.detect(entity, insured, contract, location, subscription);

        // 2. 실제 엔티티 업데이트
        entityMapper.updateEntity(entity, insured, contract, location, subscription);

        return diffs;
    }

    public Integer create(InsuredInfo insured, ContractInfo contract, BusinessLocationInfo location,
            InsuredSubscriptionInfo subscription) {
        // 1. 키 생성
        String referIdx = keyGenerator.generate();

        // 2. 엔티티 변환 및 저장 (Mapper 활용)
        TotalFormMemberEntity entity = entityMapper.toEntity(referIdx, insured, contract, location, subscription);

        return totalFormMemberRepository.save(entity).getId();
    }

}
