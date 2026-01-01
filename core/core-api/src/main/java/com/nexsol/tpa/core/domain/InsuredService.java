package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.DomainPage;
import com.nexsol.tpa.core.support.OffsetLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class InsuredService {

    private final InsuredContractFinder insuredContractFinder;

    private final InsuredContractorWriter insuredContractorWriter;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public DomainPage<InsuredContract> getList(InsuredSearchCondition condition, OffsetLimit offsetLimit) {
        return insuredContractFinder.find(condition, offsetLimit);
    }

    @Transactional(readOnly = true)
    public InsuredContractDetail getDetail(Integer id) {
        return insuredContractFinder.findDetail(id);
    }

    @Transactional
    public Integer modify(Integer id, InsuredInfo info, InsuredContractInfo contract, String memoContent,
                          Long adminId) {
        // 1. DB 업데이트 (계약 정보 수정)
        Integer updatedId = insuredContractorWriter.write(id, info, contract);

        // 2. 메모가 있다면 이벤트 발행 (작성자 ID 포함)
        if (StringUtils.hasText(memoContent)) {
            // 이벤트 발행 -> 리스너에서 FeignClient 호출

            String token = getJwtToken();
            eventPublisher.publishEvent(new InsuredModifiedEvent(id, memoContent, String.valueOf(adminId), token));
        }

        return updatedId;
    }

    private String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("Authorization");
        }
        return null;
    }

}
