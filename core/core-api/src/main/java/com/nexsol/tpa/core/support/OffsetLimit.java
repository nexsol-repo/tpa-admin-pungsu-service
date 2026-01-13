package com.nexsol.tpa.core.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

public record OffsetLimit(int offset, int limit, String sortBy, String direction) {

    // 기존 생성자 호환성을 위해 추가 (선택 사항)
    public OffsetLimit(int offset, int limit) {
        this(offset, limit, null, null);
    }

    public Pageable toPageable() {
        // 1. sortBy가 없으면 기본값으로 "id" (또는 "applicationDate") 사용
        String sortProperty = StringUtils.hasText(sortBy) ? sortBy : "id";

        // 2. direction이 없거나 "DESC"일 경우 내림차순, 그 외는 오름차순
        Sort sort = "ASC".equalsIgnoreCase(direction) ? Sort.by(sortProperty).ascending()
                : Sort.by(sortProperty).descending();

        return PageRequest.of(offset / limit, limit, sort);
    }
}