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
        exclusions.append("/iot/api/login.appUserRegister,");
        exclusions.append("/iot/api/login.appUserLogin,");
        exclusions.append("/iot/api/wechat.wechatRefreshOpenId,");
        exclusions.append("/iot/api/wechat.wechatNotifyOpenId,");
        exclusions.append("/iot/api/wechat.wechatNotifyOpenId/*,");
        exclusions.append("/app/payment/notify/*,");// 通用通知放开
        exclusions.append("/iot/api/system.listSystemInfo,");





        exclusions.append("/iot/api/login.generatorValidateCode,");
        exclusions.append("/iot/api/chargeMachine.listChargeMachine,");//查询充电桩
        exclusions.append("/iot/api/chargeMachine.listChargeMachinePort,");//查询充电桩插槽

        exclusions.append("/iot/api/wechat.getCommunityWechatAppId,");//根据小区查询appId
        // todo 道闸相关
        exclusions.append("/iot/api/barrier.customCarInOut,");//根据小区查询appId
        exclusions.append("/iot/api/tempCarFee.getTempCarFeeOrder,");//根据小区查询appId
        exclusions.append("/iot/api/parkingCoupon.listParkingCouponCar,");//根据小区查询appId
        exclusions.append("/iot/api/parkingCoupon.saveParkingCouponCar,");//根据小区查询appId
        exclusions.append("/iot/api/carMonth.queryMonthCardByCarNum,");//根据小区查询appId
        exclusions.append("/iot/api/car.queryWaitPayFeeTempCar,");//根据小区查询appId

        // todo 支付
        exclusions.append("/iot/api/payment.cashier,");//支付




        exclusions.append("/app/charge/*,");// 通用充电桩通知放开
        exclusions.append("/app/equipments/*,");// 叮叮充电桩通知放开
        exclusions.append("/app/charge/kehang/*,");// 科航充电桩通知放开
        exclusions.append("/app/smartMeter/notify/*,");// 水电表通知放开

        exclusions.append("/iot/api/visit.saveVisit,");// 访客登记
        exclusions.append("/iot/api/visitType.listVisitType,");// 访客类型
        exclusions.append("/iot/api/visit.listVisit,");// 访客人员
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
