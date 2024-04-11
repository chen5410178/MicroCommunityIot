package com.java110.meter.factory;

import com.java110.bean.ResultVo;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.fee.FeeAttrDto;
import com.java110.dto.fee.FeeDto;
import com.java110.dto.meter.MeterMachineDetailDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineFactoryDto;
import com.java110.dto.payFeeBatch.PayFeeBatchDto;
import com.java110.intf.community.ICommunityMemberV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineDetailV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineFactoryV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.meter.IPayFeeBatchV1InnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import com.java110.po.meter.MeterMachineDetailPo;
import com.java110.po.meter.MeterMachinePo;
import com.java110.po.payFeeBatch.PayFeeBatchPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartMeterCoreReadImpl implements ISmartMeterCoreRead {

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;

//    @Autowired
//    private IPayFeeV1InnerServiceSMO payFeeV1InnerServiceSMOImpl;
//
//    @Autowired
//    private IFeeAttrInnerServiceSMO feeAttrInnerServiceSMOImpl;

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;


//    @Autowired
//    private IMeterWaterV1InnerServiceSMO meterWaterV1InnerServiceSMOImpl;
//
//
//    @Autowired
//    private IMeterWaterInnerServiceSMO meterWaterInnerServiceSMOImpl;

    @Autowired
    private ICommunityMemberV1InnerServiceSMO communityMemberV1InnerServiceSMOImpl;

    @Autowired
    private IPayFeeBatchV1InnerServiceSMO payFeeBatchV1InnerServiceSMOImpl;
//
//    @Autowired
//    private IPayFeeConfigV1InnerServiceSMO payFeeConfigV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineFactoryV1InnerServiceSMO meterMachineFactoryV1InnerServiceSMOImpl;

    @Override
    public void saveMeterAndCreateFee(MeterMachineDetailDto meterMachineDetailDto, String degree, String remainingAmount, String batchId) {

        MeterMachineDto meterMachineDto = new MeterMachineDto();
        meterMachineDto.setMachineId(meterMachineDetailDto.getMachineId());
        meterMachineDto.setCommunityId(meterMachineDetailDto.getCommunityId());
        List<MeterMachineDto> meterMachineDtos = meterMachineV1InnerServiceSMOImpl.queryMeterMachines(meterMachineDto);
        Assert.listOnlyOne(meterMachineDtos, "表不存在");

        String preDegrees = "0";

        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setDetailId(meterMachineDetailDto.getDetailId());
        meterMachineDetailPo.setCurDegrees(degree);
        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_C);
        meterMachineDetailPo.setPrestoreDegrees(preDegrees);
        meterMachineDetailPo.setCurReadingTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachineDetailV1InnerServiceSMOImpl.updateMeterMachineDetail(meterMachineDetailPo);


        MeterMachinePo meterMachinePo = new MeterMachinePo();
        meterMachinePo.setMachineId(meterMachineDtos.get(0).getMachineId());
        meterMachinePo.setCurDegrees(remainingAmount);//这里用作剩余金额
        meterMachinePo.setPrestoreDegrees(degree);
        meterMachinePo.setCurReadingTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        meterMachineV1InnerServiceSMOImpl.updateMeterMachine(meterMachinePo);


    }

    /**
     * 生成批次号
     *
     * @param communityId
     */
    public String generatorBatch(String communityId) {
        PayFeeBatchPo payFeeBatchPo = new PayFeeBatchPo();
        payFeeBatchPo.setBatchId(GenerateCodeFactory.getGeneratorId("12"));
        payFeeBatchPo.setCommunityId(communityId);
        payFeeBatchPo.setCreateUserId("-1");
        payFeeBatchPo.setCreateUserName("电表推送");
        payFeeBatchPo.setState(PayFeeBatchDto.STATE_NORMAL);
        payFeeBatchPo.setMsg("正常");
        int flag = payFeeBatchV1InnerServiceSMOImpl.savePayFeeBatch(payFeeBatchPo);

        if (flag < 1) {
            throw new IllegalArgumentException("生成批次失败");
        }

        return payFeeBatchPo.getBatchId();
    }

    @Override
    public double getMeterDegree(MeterMachineDto meterMachineDto) {

        MeterMachineFactoryDto meterMachineFactoryDto = new MeterMachineFactoryDto();
        meterMachineFactoryDto.setFactoryId(meterMachineDto.getImplBean());
        List<MeterMachineFactoryDto> meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
        Assert.listOnlyOne(meterMachineFactoryDtos, "智能水电表厂家不存在");
        ISmartMeterFactoryAdapt smartMeterFactoryAdapt = null;
        try {
            smartMeterFactoryAdapt = ApplicationContextFactory.getBean(meterMachineFactoryDtos.get(0).getBeanImpl(), ISmartMeterFactoryAdapt.class);
        } finally {
            if (smartMeterFactoryAdapt == null) {
                throw new CmdException("厂家接口未实现");
            }
        }

        ResultVo resultVo = smartMeterFactoryAdapt.requestRead(meterMachineDto);

        if(ResultVo.CODE_OK != resultVo.getCode()){
            return 0.0;
        }

        if(resultVo.getData() == null){
            return 0.0;
        }
        return Double.parseDouble(resultVo.getData().toString());
    }

    @Override
    public void queryMeterMachineState(List<MeterMachineDto> meterMachineDtos) {
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {
            try {
                MeterMachineFactoryDto meterMachineFactoryDto = new MeterMachineFactoryDto();
                meterMachineFactoryDto.setFactoryId(meterMachineDto.getImplBean());
                List<MeterMachineFactoryDto> meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
                Assert.listOnlyOne(meterMachineFactoryDtos, "智能水电表厂家不存在");

                ISmartMeterFactoryAdapt smartMeterFactoryAdapt = null;
                try {
                    smartMeterFactoryAdapt = ApplicationContextFactory.getBean(meterMachineFactoryDtos.get(0).getBeanImpl(), ISmartMeterFactoryAdapt.class);
                } finally {
                    if (smartMeterFactoryAdapt == null) {
                        throw new CmdException("厂家接口未实现");
                    }
                }

                smartMeterFactoryAdapt.queryMeterMachineState(meterMachineDto);
            } catch (CmdException e) {
                e.printStackTrace();
                meterMachineDto.setState(MeterMachineDto.STATE_OFFLINE);
                meterMachineDto.setStateName("离线");
            }
        }
    }

    @Override
    public ResultVo cleanControl(MeterMachineDto meterMachineDto) {
        try {
            MeterMachineFactoryDto meterMachineFactoryDto = new MeterMachineFactoryDto();
            meterMachineFactoryDto.setFactoryId(meterMachineDto.getImplBean());
            List<MeterMachineFactoryDto> meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
            Assert.listOnlyOne(meterMachineFactoryDtos, "智能水电表厂家不存在");

            ISmartMeterFactoryAdapt smartMeterFactoryAdapt = null;
            try {
                smartMeterFactoryAdapt = ApplicationContextFactory.getBean(meterMachineFactoryDtos.get(0).getBeanImpl(), ISmartMeterFactoryAdapt.class);
            } finally {
                if (smartMeterFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
            }

            return smartMeterFactoryAdapt.cleanControl(meterMachineDto);
        } catch (CmdException e) {
            e.printStackTrace();
            return new ResultVo(ResultVo.CODE_ERROR, "失败");
        }
    }

    @Override
    public ResultVo switchControl(MeterMachineDto meterMachineDto, String controlType) {
        try {
            MeterMachineFactoryDto meterMachineFactoryDto = new MeterMachineFactoryDto();
            meterMachineFactoryDto.setFactoryId(meterMachineDto.getImplBean());
            List<MeterMachineFactoryDto> meterMachineFactoryDtos = meterMachineFactoryV1InnerServiceSMOImpl.queryMeterMachineFactorys(meterMachineFactoryDto);
            Assert.listOnlyOne(meterMachineFactoryDtos, "智能水电表厂家不存在");

            ISmartMeterFactoryAdapt smartMeterFactoryAdapt = null;
            try {
                smartMeterFactoryAdapt = ApplicationContextFactory.getBean(meterMachineFactoryDtos.get(0).getBeanImpl(), ISmartMeterFactoryAdapt.class);
            } finally {
                if (smartMeterFactoryAdapt == null) {
                    throw new CmdException("厂家接口未实现");
                }
            }

            return smartMeterFactoryAdapt.switchControl(meterMachineDto, controlType);
        } catch (CmdException e) {
            e.printStackTrace();
            return new ResultVo(ResultVo.CODE_ERROR, "失败");
        }
    }
}
