// src/main/java/com/ex/boot/config/WebConfig.java
package com.ex.boot.config;

import com.ex.boot.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")      // 拦截所有以 / 开头的接口
                .excludePathPatterns(            // 白名单
                        "/login",    // 原路径
                        "/register",  // 原路径
                        "/index"  // 原路径
                );
    }
}
