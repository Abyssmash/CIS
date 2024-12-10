package com.cis.personal_task.taskFilter;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<LoginServlet> loginServlet() {
        ServletRegistrationBean<LoginServlet> registrationBean = new ServletRegistrationBean<>(new LoginServlet(), "/login");
        registrationBean.setName("LoginServlet");
        return registrationBean;
    }
}
