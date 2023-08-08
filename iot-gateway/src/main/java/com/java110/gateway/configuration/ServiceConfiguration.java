package com.java110.gateway.configuration;

import com.java110.gateway.filter.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wuxw on 2018/5/2.
 */
@Configuration
public class ServiceConfiguration {

    public static final  StringBuffer exclusions = new StringBuffer();

    static {
        exclusions.append("/iot/api/login.pcUserLogin,");
        exclusions.append("/iot/api/login.getSysInfo,");

        exclusions.append("/iot/api/login.generatorValidateCode,");
    }
    @Bean
    public FilterRegistrationBean jwtFilter() {

        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/iot/api/*");
        registrationBean.addInitParameter("excludedUri",exclusions.toString());

        return registrationBean;
    }

}
