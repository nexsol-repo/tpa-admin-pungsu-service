package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeritzService {

    private final MeritzFinder meritzFinder;

    @Transactional(readOnly = true)
    public String getLink4(String prctrNo) {
        return meritzFinder.find(prctrNo);
    }

}
