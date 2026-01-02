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

import java.util.List;
import java.util.stream.Collectors;

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

        List<ChangeDetail> diffs = insuredContractorWriter.writeAndGetDiff(id, info, contract);
        String token = getJwtToken();

        if (!diffs.isEmpty()) {
            String systemMemo = "시스템 변경 로그: " + diffs.stream()
                    .map(ChangeDetail::toString)
                    .collect(Collectors.joining(", "));

            // memo-service의 'SYSTEM' 분류로 전송하기 위한 이벤트
            eventPublisher.publishEvent(new InsuredModifiedEvent(id, systemMemo, String.valueOf(adminId), token));
        }

        // 3. 관리자가 직접 작성한 메모가 있다면 이벤트 발행
        if (StringUtils.hasText(memoContent)) {
            eventPublisher.publishEvent(new InsuredModifiedEvent(id, memoContent, String.valueOf(adminId), token));
        }

        return id;
    }

    private String getJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("Authorization");
        }
        return null;
    }

}
