package com.nexsol.tpa.core.support;

import java.util.List;

public record DomainPage<T>(List<T> content, boolean hasNext) {
}
