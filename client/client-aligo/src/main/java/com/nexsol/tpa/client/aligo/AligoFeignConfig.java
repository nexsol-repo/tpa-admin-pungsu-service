package com.nexsol.tpa.client.aligo;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverters;

public class AligoFeignConfig {

    @Bean
    public Encoder feignFormEncoder(ObjectProvider<FeignHttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}