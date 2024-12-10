package com.cis.personal_task.taskFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilter() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>(new AuthFilter());
        registrationBean.addUrlPatterns("/tasks/*");  // 인증이 필요한 URL에 필터 적용
        registrationBean.setOrder(1);  // 필터 순서 설정
        return registrationBean;
    }
}

