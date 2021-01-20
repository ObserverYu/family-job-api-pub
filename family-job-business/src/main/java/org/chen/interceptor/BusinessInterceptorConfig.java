package org.chen.interceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器
 *
 * @author YuChen
 * @date 2020-5-28 14:29:57
 **/
@Configuration
public class BusinessInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private JWTInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先入后出
        registry.addInterceptor(jwtInterceptor)
                .order(Integer.MAX_VALUE-2)
                .addPathPatterns("/**");

    }
}
