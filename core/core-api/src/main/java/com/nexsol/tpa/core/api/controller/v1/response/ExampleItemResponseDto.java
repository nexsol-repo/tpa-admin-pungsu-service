package com.nexsol.tpa.core.api.controller.v1.response;

import java.util.List;

public record ExampleItemResponseDto(String key) {
    public static List<ExampleItemResponseDto> build() {
        return List.of(new ExampleItemResponseDto("1"), new ExampleItemResponseDto("2"));
    }
}