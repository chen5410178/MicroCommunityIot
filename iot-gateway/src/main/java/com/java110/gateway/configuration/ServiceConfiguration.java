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

    public static final StringBuffer exclusions = new StringBuffer();

    static {
        exclusions.append("/iot/api/login.pcUserLogin,");
        exclusions.append("/iot/api/login.ssoTokenLogin,");

        exclusions.append("/iot/api/login.getSysInfo,");
        exclusions.append("/iot/api/login.appUserRegister,");
        exclusions.append("/iot/api/login.appUserLogin,");
        exclusions.append("/iot/api/login.phoneWechatLogin,");
        exclusions.append("/iot/api/wechat.wechatRefreshOpenId,");
        exclusions.append("/iot/api/wechat.getWechatMiniOpenId,");
        exclusions.append("/iot/api/wechat.wechatNotifyOpenId,");
        exclusions.append("/iot/api/wechat.wechatNotifyOpenId/*,");
        exclusions.append("/app/payment/notify/*,");// 通用通知放开
        exclusions.append("/iot/api/system.listSystemInfo,");
        exclusions.append("/iot/api/system.listSystemPrivacy,");


        exclusions.append("/iot/api/login.generatorValidateCode,");
        exclusions.append("/iot/api/login.generatorSliderCode,");
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
        exclusions.append("/iot/api/car.getOrGenerateUnlicensedCarNum,");//无牌车获取或生成车牌号

        exclusions.append("/iot/api/parkingArea.findCarInParkingArea,");// 停车场找车


        // todo 支付
        exclusions.append("/iot/api/payment.cashier,");//支付


        exclusions.append("/app/charge/*,");// 通用充电桩通知放开
        exclusions.append("/app/chargeBikeMini/*,");// 充电桩小程序二维码转跳放开
        exclusions.append("/app/chargeBikeH5/*,");// 充电桩公众号二维码转跳放开
        exclusions.append("/app/chargeCarMini/*,");// 充电桩小程序二维码转跳放开
        exclusions.append("/app/chargeCarH5/*,");// 充电桩公众号二维码转跳放开
        exclusions.append("/app/chargeBikeMiniPort/*/*,");// 充电桩端口小程序二维码转跳放开
        exclusions.append("/app/chargeBikeH5Port/*/*,");// 充电桩端口公众号二维码转跳放开
        exclusions.append("/app/chargeCarMiniPort/*/*,");// 充电桩端口小程序二维码转跳放开
        exclusions.append("/app/chargeCarH5Port/*/*,");// 充电桩端口公众号二维码转跳放开
        exclusions.append("/app/equipments/*,");// 叮叮充电桩通知放开
        exclusions.append("/app/charge/kehang/*,");// 科航充电桩通知放开
        exclusions.append("/app/smartMeter/notify/*,");// 水电表通知放开

        exclusions.append("/iot/api/visit.saveVisit,");// 访客登记
        exclusions.append("/iot/api/visitType.listVisitType,");// 访客类型
        exclusions.append("/iot/api/visit.listVisit,");// 访客人员
        exclusions.append("/iot/api/visit.queryWaitVisitCarNum,");// 查询等待访客车牌号


        exclusions.append("/iot/api/personFace.savePersonFace,");// 人脸审核
        exclusions.append("/iot/api/personFace.listPersonFace,");// 人脸审核
        exclusions.append("/iot/api/upload/uploadImage,");// 上传图片
        exclusions.append("/iot/api/floor.queryFloors,");// 上传图片
        exclusions.append("/iot/api/unit.queryUnits,");// 上传图片
        exclusions.append("/iot/api/room.queryRoomsByApp,");// 查询房屋

        exclusions.append("/iot/api/passcode.applyPassQrcode,");// 申请通行码
        exclusions.append("/iot/api/passcode.refreshPassQrcode,");// 查询通信码
        exclusions.append("/iot/api/user.userSendSms,");// 发送验证码
        exclusions.append("/iot/api/user.resetUserPwd,");// 忘记密码重置（免登录）

        exclusions.append("/iot/api/meterQrcode.listMeterQrcode,");// 查询水电二维码
        exclusions.append("/iot/api/meterMachine.listMeterMachine,");// 查询水电表
        exclusions.append("/iot/api/meterMachineCharge.listMeterMachineCharge,");// 查询水电表充值记录

        exclusions.append("/iot/api/community.listCommunitys,");// 查询小区
        exclusions.append("/iot/api/user.ownerUserLoginByOpenId,");// 查询小区


        //exclusions.append("/iot/api/appUser.saveAppUser,");// 房屋认证


    }

    @Bean
    public FilterRegistrationBean jwtFilter() {

        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/iot/api/*");
        registrationBean.addUrlPatterns("/app/*");
        registrationBean.addInitParameter("excludedUri", exclusions.toString());

        return registrationBean;
    }

}
