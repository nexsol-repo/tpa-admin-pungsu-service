package com.nexsol.tpa.client.aligo.config;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectProvider;


import org.springframework.boot.http.converter.autoconfigure.ClientHttpMessageConvertersCustomizer;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;

import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

//@Configuration
// [핵심] Customizer가 실제로 동작하여 HttpMessageConverters 빈을 만들도록 AutoConfig를 가져옵니다.
//@Import(HttpMessageConvertersAutoConfiguration.class)
public class AligoFeignConfig {

//    @Bean
//    public ClientHttpMessageConvertersCustomizer aligoHtmlResponseCustomizer() {
//        return builder -> {
//            // 1. Jackson 3 컨버터 생성
//            JacksonJsonHttpMessageConverter jacksonConverter = new JacksonJsonHttpMessageConverter();
//
//            // 2. 알리고의 "text/html" 응답도 JSON으로 처리하도록 설정
//            jacksonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));
//
//            // 3. 문서에 나온대로 builder.withJsonConverter() 사용
//            builder.withJsonConverter(jacksonConverter);
//        };
//    }
@Bean
public Decoder feignDecoder() {
    // 1. 사용자님이 지정하신 Jackson 3 기반 JacksonJsonHttpMessageConverter 사용
    JacksonJsonHttpMessageConverter jacksonConverter = new JacksonJsonHttpMessageConverter();

    // 2. 알리고의 "text/html" 응답도 JSON으로 파싱하도록 명시적으로 설정
    jacksonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));

    // 3. Deprecated된 HttpMessageConverters 클래스를 거치지 않고 직접 Decoder 구현
    return (response, type) -> {
        if (response.body() == null) return null;

        // Feign Response를 Spring의 HttpInputMessage로 변환하여 컨버터에 전달
        HttpInputMessage inputMessage = new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException { return response.body().asInputStream(); }
            @Override
            public org.springframework.http.HttpHeaders getHeaders() {
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                response.headers().forEach((k, v) -> headers.addAll(k, List.copyOf(v)));
                return headers;
            }
        };
        return jacksonConverter.read((ResolvableType) type, (HttpInputMessage) null, (Map<String, Object>) inputMessage);
    };
}

    @Bean
    public Encoder feignFormEncoder(ObjectProvider<FeignHttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}