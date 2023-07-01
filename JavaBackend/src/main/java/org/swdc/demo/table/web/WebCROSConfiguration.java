package org.swdc.demo.table.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 本应用允许跨域访问
 */
@Configuration
public class WebCROSConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOriginPatterns("*") // 允许所有域
                .allowedMethods("*") // 允许任何方法（post、get等）
                .allowedHeaders("*") // 允许任何请求头
                .allowCredentials(true) // 允许证书、cookie
                .exposedHeaders(HttpHeaders.SET_COOKIE)
                .maxAge(3600L); // maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
    }
}
