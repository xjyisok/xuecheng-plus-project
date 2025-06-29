package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * @description 跨域过滤器配置，兼容 Spring Boot 3.x+
 */
@Configuration
public class GlobalCorsConfig {

    /**
     * 配置跨域过滤器，支持带 Cookie 的跨域请求
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // ✅ 替代 addAllowedOrigin("*")，用于 Spring Boot 3.x+
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        // 允许携带 cookie
        config.setAllowCredentials(true);

        // 放行全部原始头信息
        config.addAllowedHeader("*");

        // 允许所有请求方法跨域调用
        config.addAllowedMethod("*");

        // 注册跨域配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
