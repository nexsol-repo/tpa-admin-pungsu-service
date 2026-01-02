package com.nexsol.tpa.client.aligo.config;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectProvider;


import org.springframework.boot.http.converter.autoconfigure.ClientHttpMessageConvertersCustomizer;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;

import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

import java.util.List;

@Configuration
// [핵심] Customizer가 실제로 동작하여 HttpMessageConverters 빈을 만들도록 AutoConfig를 가져옵니다.
@Import(HttpMessageConvertersAutoConfiguration.class)
public class AligoFeignConfig {

    @Bean
    public ClientHttpMessageConvertersCustomizer aligoHtmlResponseCustomizer() {
        return builder -> {
            // 1. Jackson 3 컨버터 생성
            JacksonJsonHttpMessageConverter jacksonConverter = new JacksonJsonHttpMessageConverter();

            // 2. 알리고의 "text/html" 응답도 JSON으로 처리하도록 설정
            jacksonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));

            // 3. 문서에 나온대로 builder.withJsonConverter() 사용
            builder.withJsonConverter(jacksonConverter);
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