package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import com.nexsol.tpa.storage.db.core.MeritzSixEntity;
import com.nexsol.tpa.storage.db.core.MeritzSixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeritzFinder {

    private final MeritzSixRepository meritzSixRepository;

    public String find(String prctrNo) {
        if (prctrNo == null) {
            return null;
        }
        return meritzSixRepository.findByPrctrNoAndErrCd(prctrNo, "00001")
            .map(MeritzSixEntity::getRltLinkUrl4)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND_DATA));
    }

}
