package com.nexsol.tpa.core.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // [문서 경로 매핑] /docs/** 요청이 오면
        registry.addResourceHandler("/docs/**")
            // 1. (배포 시) JAR 내부 classpath:/static/docs/ 에서 찾음
            .addResourceLocations("classpath:/static/docs/")
            // 2. (로컬 개발 시) build/docs/asciidoc/ 폴더에서 직접 찾음 (복사 불필요!)
            .addResourceLocations("file:build/docs/asciidoc/");
    }

}
