package com.nexsol.tpa.client.example;

import com.nexsol.tpa.client.example.model.ExampleClientResult;

record ExampleResponseDto(String exampleResponseValue) {
    ExampleClientResult toResult() {
        return new ExampleClientResult(exampleResponseValue);
    }
}
