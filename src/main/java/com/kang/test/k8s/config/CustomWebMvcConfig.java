package com.kang.test.k8s.config;

import com.kang.test.k8s.interceptor.RequestTraceIdInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author HuangKang
 * @Date 2021/4/23 下午4:00
 * @Summarize 自定义WebMvc配置
 */
@Configuration
public class CustomWebMvcConfig  implements WebMvcConfigurer {

    final RequestTraceIdInterceptor requestTraceIdInterceptor;

    @Autowired
    public CustomWebMvcConfig(RequestTraceIdInterceptor requestTraceIdInterceptor) {
        this.requestTraceIdInterceptor = requestTraceIdInterceptor;
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加TraceId拦截器
        registry.addInterceptor(requestTraceIdInterceptor);
    }
}
