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

    @Transactional(readOnly = true)
    public DomainPage<InsuredContract> getList(InsuredSearchCondition condition, OffsetLimit offsetLimit) {
        return insuredContractFinder.find(condition, offsetLimit);
    }

}
