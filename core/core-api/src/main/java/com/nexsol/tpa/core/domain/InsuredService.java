package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InsuredService {

    private final InsuredContractFinder insuredContractFinder;

    private final InsuredContractorWriter insuredContractorWriter;

    @Transactional(readOnly = true)
    public DomainPage<InsuredContract> getList(InsuredSearchCondition condition, OffsetLimit offsetLimit) {
        return insuredContractFinder.find(condition, offsetLimit);
    }

    @Transactional(readOnly = true)
    public InsuredContractDetail getDetail(Integer id) {
        return insuredContractFinder.findDetail(id);
    }

    @Transactional
    public Integer modify(Integer id, InsuredInfo info, InsuredContractInfo contract) {
        return insuredContractorWriter.write(id, info, contract);
    }

}
