package com.java110.dto.carInout;

import com.java110.bean.dto.PageDto;
import com.java110.bean.dto.coupon.ParkingCouponCarDto;
import com.java110.dto.payment.CarInoutPaymentDto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName FloorDto
 * @Description 出入场明细数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class CarInoutDto extends CarInoutPaymentDto implements Serializable {
    public static final String CAR_INOUT_IN = "3306";
    public static final String CAR_INOUT_OUT = "3307";

    //状态，100300 进场状态 100400 支付完成 100500 离场状态 100600 支付超时重新支付
    public static final String STATE_IN = "100300";
    public static final String STATE_PAY = "100400";
    public static final String STATE_OUT = "100500";
    public static final String STATE_REPAY = "100600";

    public static final String STATE_IN_FAIL = "100301";
    public static final String CAR_TYPE_MONTH = "1001";
    public static final String CAR_TYPE_TEMP = "1003";

    private String paNum;
    private String machineCode;

    private String machineName;
    private String carInout;
    private String photoJpg;
    private String carNum;
    private String carNumLike;
    private String remark;
    private String ciId;
    private String inoutId;
    private String machineId;
    private String carType;
    private String paId;
    private String[] paIds;
    private String state;

    private String stateName;
    private String[] states;
    private String communityId;
    private String[] communityIds;
    private String communityName;
    private String openTime;
    private String carTypeName;


    private Date createTime;

    private String statusCd = "0";

    private Date payTime;

    private String inTime;

    private long hours;
    private long min;

    private String feeName;

    private String configId;

    private String unlicense;

    private String colorType;


    private List<ParkingCouponCarDto> parkingCouponCarDtos;


    public String getPaNum() {
        return paNum;
    }

    public void setPaNum(String paNum) {
        this.paNum = paNum;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getCarInout() {
        return carInout;
    }

    public void setCarInout(String carInout) {
        this.carInout = carInout;
    }

    public String getPhotoJpg() {
        return photoJpg;
    }

    public void setPhotoJpg(String photoJpg) {
        this.photoJpg = photoJpg;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCiId() {
        return ciId;
    }

    public void setCiId(String ciId) {
        this.ciId = ciId;
    }

    public String getInoutId() {
        return inoutId;
    }

    public void setInoutId(String inoutId) {
        this.inoutId = inoutId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String[] getStates() {
        return states;
    }

    public void setStates(String[] states) {
        this.states = states;
    }

    public String[] getPaIds() {
        return paIds;
    }

    public void setPaIds(String[] paIds) {
        this.paIds = paIds;
    }



    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    @Override
    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public List<ParkingCouponCarDto> getParkingCouponCarDtos() {
        return parkingCouponCarDtos;
    }

    public void setParkingCouponCarDtos(List<ParkingCouponCarDto> parkingCouponCarDtos) {
        this.parkingCouponCarDtos = parkingCouponCarDtos;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    @Override
    public String getStateName() {
        return stateName;
    }

    @Override
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getUnlicense() {
        return unlicense;
    }

    public void setUnlicense(String unlicense) {
        this.unlicense = unlicense;
    }

    public String getCarNumLike() {
        return carNumLike;
    }

    public void setCarNumLike(String carNumLike) {
        this.carNumLike = carNumLike;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String[] getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(String[] communityIds) {
        this.communityIds = communityIds;
    }

    public String getColorType() {
        return colorType;
    }

    public void setColorType(String colorType) {
        this.colorType = colorType;
    }
}
