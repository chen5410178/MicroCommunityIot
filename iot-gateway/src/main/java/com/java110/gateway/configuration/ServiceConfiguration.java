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
        exclusions.append("/app/chargeMachine.listChargeMachine,");//查询充电桩
        exclusions.append("/app/chargeMachine.listChargeMachinePort,");//查询充电桩插槽
        exclusions.append("/app/charge/*,");// 通用充电桩通知放开
        exclusions.append("/app/equipments/*,");// 叮叮充电桩通知放开
        exclusions.append("/app/charge/kehang/*,");// 科航充电桩通知放开
    }
    @Bean
    public FilterRegistrationBean jwtFilter() {

        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/iot/api/*");
        registrationBean.addUrlPatterns("/app/*");
        registrationBean.addInitParameter("excludedUri",exclusions.toString());

        return registrationBean;
    }

}
