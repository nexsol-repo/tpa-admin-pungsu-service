package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.MeritzAreaCodeEntity;
import com.nexsol.tpa.storage.db.core.MeritzAreaCodeRepository;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import com.nexsol.tpa.storage.db.core.TotalFormMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuredContractorWriter {

    private final TotalFormMemberRepository totalFormMemberRepository;

    private final DirectRegistration directRegistration;

    private final ContractKeyGenerator keyGenerator;

    private final InsuredEntityMapper entityMapper;

    private final ContractChangeDetector changeDetector;

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

    public Integer write(InsuredInfo insured, ContractInfo contract, BusinessLocationInfo location,
            InsuredSubscriptionInfo subscription) {
        // 1. 키 생성
        String referIdx = keyGenerator.generate();

        // 2. 엔티티 변환 및 저장 (Mapper 활용)
        TotalFormMemberEntity entity = entityMapper.toEntity(referIdx, "OFFLINE", insured, contract, location,
                subscription);

        // 3. 건물급수,지역코드 계산 및 적용
        directRegistration.applyDerivedFields(entity, location);

        return totalFormMemberRepository.save(entity).getId();
    }

    @Transactional
    public void confirmFreeContract(FreeContractUpdateInfo info) {

        TotalFormMemberEntity entity = totalFormMemberRepository
            .findFirstByBusinessNumberAndAddressContaining(info.businessNo(), info.address())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA,
                    "해당 사업자번호와 주소를 가진 계약을 찾을 수 없습니다: " + info.businessNo()));

        // 2. 엔티티 상태 변경 (기존 동일)
        entity.updateFreeContract(info.securityNo(), info.insuranceDate(), info.insuranceEndDate(), info.totalPremium(),
                info.govPremium(), info.localPremium(), info.ownerPremium());
    }

}
