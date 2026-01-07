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
        Sort sort = Sort.unsorted();

        if (StringUtils.hasText(sortBy)) {
            sort = "DESC".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        }

        return PageRequest.of(offset / limit, limit, sort);
    }
}