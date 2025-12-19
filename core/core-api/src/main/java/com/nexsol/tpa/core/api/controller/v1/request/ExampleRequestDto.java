package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.ExampleData;

public record ExampleRequestDto(String data) {
    public ExampleData toExampleData() {
        return new ExampleData(data, data);
    }
}
