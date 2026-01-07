package com.nexsol.tpa.core.support.response;

import java.util.List;

public record PageResponse<T>(List<T> content, boolean hasNext,long totalElements) {
}
