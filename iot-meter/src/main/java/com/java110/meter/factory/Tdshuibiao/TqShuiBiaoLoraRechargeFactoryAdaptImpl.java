package com.java110.meter.factory.Tdshuibiao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.cache.UrlCache;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.ListUtil;
import com.java110.dto.meter.MeterMachineDetailDto;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.dto.meter.MeterMachineSpecDto;
import com.java110.intf.meter.IMeterMachineChargeV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineDetailV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineSpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.meter.factory.ISmartMeterCoreRead;
import com.java110.meter.factory.ISmartMeterFactoryAdapt;
import com.java110.meter.factory.tqdianbiao.TdDianBiaoUtil;
import com.java110.po.meter.MeterMachineDetailPo;
import com.java110.po.meterMachineCharge.MeterMachineChargePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
   拓强智能水表 处理类 -- 6.3 Lora普通预付费水表
   http://doc-api.tqdianbiao.com/#/api2/6/2/1
 */
@Service("tqShuiBiaoLoraRechargeFactoryAdaptImpl")
public class TqShuiBiaoLoraRechargeFactoryAdaptImpl implements ISmartMeterFactoryAdapt {
    private static final String READ_URL = "http://api2.tqdianbiao.com/Api_v2/water_read";

    private static final String RECHARGE_URL = "http://api2.tqdianbiao.com/Api_v2/water_security/recharge";

    private static final String NOTIFY_URL = "/app/smartMeter/notify/d";
    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;

    private static final String ELE_CONTROL_URL = "http://api2.tqdianbiao.com/Api_v2/ele_control";

    private static final String WATER_METER_QUERY_URL = "http://api2.tqdianbiao.com/Api_v2/water_meter/query";//水表档案查询

    private static final String ELE_CONTROL_NOTIFY_URL = "/app/smartMeter/notify/e/switchControl";


    private static final String CLEAN_URL = "http://api2.tqdianbiao.com/Api_v2/ele_security/reset";//清零后必须开户才能充值

    private static final String OPEN_ACOUNT_URL = "http://api2.tqdianbiao.com/Api_v2/ele_security/open_acount";//开户

    private static final String CLEAN_NOTIFY_URL = "/app/smartMeter/notify/e/cleanControl";

    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;


    @Autowired
    private ISmartMeterCoreRead smartMeterCoreReadImpl;

    @Autowired
    private IMeterMachineChargeV1InnerServiceSMO meterMachineChargeV1InnerServiceSMOImpl;

    @Override
    public ResultVo requestRecharge(MeterMachineDto meterMachineDto, double degree, double money) {
        List<Map<String, Object>> req = new ArrayList<>();


        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120104");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (meterMachineSpecDtos == null || meterMachineSpecDtos.size() < 1) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }
        String detailId = GenerateCodeFactory.getGeneratorId("11");
        Map<String, Object> item = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("money", money);

        item.put("opr_id", detailId);
        item.put("time_out", 0);
        item.put("must_online", true);
        item.put("retry_times", 1);
        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        item.put("address", meterMachineDto.getAddress());
        item.put("params", params);
        req.add(item);


        String request_content = JSON.toJSONString(req);

        String resStr = TdDianBiaoUtil.requestAsync(RECHARGE_URL, request_content, UrlCache.getOwnerUrl() + NOTIFY_URL);

        JSONObject paramOut = JSONObject.parseObject(resStr);

        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            return new ResultVo(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }

        JSONArray responseContent = paramOut.getJSONArray("response_content");

        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if (!"SUCCESS".equals(status)) {
                return new ResultVo(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }

        MeterMachineChargePo meterMachineChargePo = new MeterMachineChargePo();
        meterMachineChargePo.setChargeId(GenerateCodeFactory.getGeneratorId("11"));
        meterMachineChargePo.setMachineId(meterMachineDto.getMachineId());
        meterMachineChargePo.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineChargePo.setChargeMoney(money + "");
        meterMachineChargePo.setChargeDegrees(degree + "");
        meterMachineChargePo.setState("C");
        int flag = meterMachineChargeV1InnerServiceSMOImpl.saveMeterMachineCharge(meterMachineChargePo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

//        List<MeterMachineDetailPo> meterMachineDetailPos = new ArrayList<>();
//        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
//        meterMachineDetailPo.setCommunityId(meterMachineDto.getCommunityId());
//        meterMachineDetailPo.setDetailId(detailId);
//        meterMachineDetailPo.setMachineId(meterMachineDto.getMachineId());
//        meterMachineDetailPo.setDetailType(meterMachineDto.getMachineModel()); // 抄表
//        meterMachineDetailPo.setCurDegrees((Double.parseDouble(meterMachineDto.getCurDegrees()) + degree) + "");
//        meterMachineDetailPo.setCurReadingTime(meterMachineDto.getCurReadingTime());
//        meterMachineDetailPo.setPrestoreDegrees((Double.parseDouble(meterMachineDto.getCurDegrees()) + degree) + "");
//        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_C);
//        meterMachineDetailPos.add(meterMachineDetailPo);
//
//        if (meterMachineDetailPos.size() > 0) {
//            meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetails(meterMachineDetailPos);
//        }

        //todo 下发查询余额
        requestRead(meterMachineDto);

        return new ResultVo(ResultVo.CODE_OK, "提交成功");
    }

    @Override
    public ResultVo requestRead(MeterMachineDto meterMachineDto) {
        List<Map<String, Object>> req = new ArrayList<>();


        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120104");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (ListUtil.isNull(meterMachineSpecDtos)) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }
        String detailId = GenerateCodeFactory.getGeneratorId("11");
        Map<String, Object> item = new HashMap<>();
        item.put("opr_id", detailId);
        item.put("time_out", 3);
        item.put("must_online", true);
        item.put("retry_times", 1);
        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        item.put("address", meterMachineDto.getAddress());
        item.put("type", 42);
        req.add(item);
        List<MeterMachineDetailPo> meterMachineDetailPos = new ArrayList<>();
        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineDetailPo.setDetailId(detailId);
        meterMachineDetailPo.setMachineId(meterMachineDto.getMachineId());
        meterMachineDetailPo.setDetailType(meterMachineDto.getMachineModel()); // 抄表
        meterMachineDetailPo.setCurDegrees(meterMachineDto.getCurDegrees());
        meterMachineDetailPo.setCurReadingTime(meterMachineDto.getCurReadingTime());
        meterMachineDetailPo.setPrestoreDegrees(meterMachineDto.getPrestoreDegrees());
        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_W);
        meterMachineDetailPos.add(meterMachineDetailPo);

        String request_content = JSON.toJSONString(req);

        String resStr = TdDianBiaoUtil.requestAsync(READ_URL, request_content, UrlCache.getOwnerUrl() + NOTIFY_URL);

        JSONObject paramOut = JSONObject.parseObject(resStr);

        //{"response_content":"[{\"opr_id\":\"112024051400320889\",\"status\":\"SUCCESS\"}]",
        // "status":"SUCCESS","timestamp":1715651107,"sign":"9711ba369d7ef880a51eca32632d2b08"}
        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            meterMachineDto.setState(MeterMachineDto.STATE_OFFLINE);
            meterMachineDto.setStateName(paramOut.getString("error_msg"));
            return new ResultVo(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }

        JSONArray responseContent = paramOut.getJSONArray("response_content");
        meterMachineDto.setState(MeterMachineDto.STATE_ONLINE);
        meterMachineDto.setStateName("在线");//默认在线
        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if (!"SUCCESS".equals(status)) {
                meterMachineDto.setState(MeterMachineDto.STATE_OFFLINE);
                meterMachineDto.setStateName(responseContent.getJSONObject(resIndex).getString("error_msg"));
                return new ResultVo(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }
        if (meterMachineDetailPos.size() > 0) {
            meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetails(meterMachineDetailPos);
        }
        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电表反馈数据");
    }

    @Override
    public ResultVo requestReads(List<MeterMachineDto> meterMachineDtos) {
        List<Map<String, Object>> req = new ArrayList<>();
        List<MeterMachineDetailPo> meterMachineDetailPos = new ArrayList<>();
        String detailId = "";
        for (MeterMachineDto meterMachineDto : meterMachineDtos) {

            MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
            meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
            meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
            meterMachineSpecDto.setSpecId("120104");
            List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
            if (meterMachineSpecDtos == null || meterMachineSpecDtos.size() < 1) {
                continue;
            }
            detailId = GenerateCodeFactory.getGeneratorId("11");
            Map<String, Object> item = new HashMap<>();
            item.put("opr_id", detailId);
            item.put("time_out", 0);
            item.put("must_online", true);
            item.put("retry_times", 1);
            item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
            item.put("address", meterMachineDto.getAddress());
            item.put("type", 42);
            req.add(item);

            MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
            meterMachineDetailPo.setCommunityId(meterMachineDto.getCommunityId());
            meterMachineDetailPo.setDetailId(detailId);
            meterMachineDetailPo.setMachineId(meterMachineDto.getMachineId());
            meterMachineDetailPo.setDetailType(meterMachineDto.getMachineModel()); // 抄表
            meterMachineDetailPo.setCurDegrees(meterMachineDto.getCurDegrees());
            meterMachineDetailPo.setCurReadingTime(meterMachineDto.getCurReadingTime());
            meterMachineDetailPo.setPrestoreDegrees(meterMachineDto.getPrestoreDegrees());
            meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_W);
            meterMachineDetailPos.add(meterMachineDetailPo);

        }

        String request_content = JSON.toJSONString(req);

        String resStr = TdDianBiaoUtil.requestAsync(READ_URL, request_content, UrlCache.getOwnerUrl() + NOTIFY_URL);

        JSONObject paramOut = JSONObject.parseObject(resStr);

        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            return new ResultVo(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }

        JSONArray responseContent = JSONArray.parseArray(paramOut.getString("response_content"));

        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if (!"SUCCESS".equals(status)) {
                return new ResultVo(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }

        if (meterMachineDetailPos.size() > 0) {
            meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetails(meterMachineDetailPos);
        }

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电表反馈数据");
    }

    /**
     * 抄表异步通知
     *
     * @param readData
     * @return
     */
    @Override
    public ResultVo notifyReadData(String readData) {

        JSONObject data = JSONObject.parseObject(readData);

        String response_content = data.getString("response_content");
        String timestamp = data.getString("timestamp");
        String sign = data.getString("sign");
// 验签
        if (!TdDianBiaoUtil.checkSign(response_content, timestamp, sign)) {
            System.out.println("sign check failed");
            return new ResultVo(ResultVo.CODE_ERROR, "鉴权失败");
        }

        JSONArray contentArray = JSON.parseArray(response_content);

        if (ListUtil.isNull(contentArray)) {
            return new ResultVo(ResultVo.CODE_ERROR, "没有数据");
        }

        MeterMachineDetailDto meterMachineDetailDto = new MeterMachineDetailDto();
        meterMachineDetailDto.setDetailId(contentArray.getJSONObject(0).getString("opr_id"));
        meterMachineDetailDto.setState(MeterMachineDetailDto.STATE_W);
        List<MeterMachineDetailDto> meterMachineDetailDtos = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMachineDetails(meterMachineDetailDto);
        if (ListUtil.isNull(meterMachineDetailDtos)) {
            return new ResultVo(ResultVo.CODE_ERROR, "没有数据");
        }

        //判断是不是充值订单，是的话，完成充值
        if ("2002".equals(meterMachineDetailDtos.get(0).getDetailType())) {
            MeterMachineChargePo meterMachineChargePo = new MeterMachineChargePo();
            meterMachineChargePo.setChargeId(contentArray.getJSONObject(0).getString("opr_id"));
            meterMachineChargePo.setState("C");
            meterMachineChargePo.setRemark("拓强异步通知，充值成功。");
            meterMachineChargeV1InnerServiceSMOImpl.updateMeterMachineCharge(meterMachineChargePo);
            return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
        }

        String batchId = smartMeterCoreReadImpl.generatorBatch(meterMachineDetailDtos.get(0).getCommunityId());


        for (int i = 0; i < contentArray.size(); ++i) {
            try {
                JSONObject contentObject = contentArray.getJSONObject(i);
                doBusiness(contentObject, batchId);
            } catch (Exception e) {
                System.out.println("异常数据：" + response_content);
                e.printStackTrace();
            }


        }

        return new ResultVo(ResultVo.CODE_OK, "成功");
    }

    private void doBusiness(JSONObject contentObject, String batchId) {
        MeterMachineDetailDto meterMachineDetailDto;
        meterMachineDetailDto = new MeterMachineDetailDto();
        meterMachineDetailDto.setDetailId(contentObject.getString("opr_id"));
        meterMachineDetailDto.setState(MeterMachineDetailDto.STATE_W);
        List<MeterMachineDetailDto> meterMachineDetailDtos = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMachineDetails(meterMachineDetailDto);
        if (meterMachineDetailDtos == null || meterMachineDetailDtos.size() < 1) {
            return;
        }

        if (!"SUCCESS".equals(contentObject.getString("status"))) {
            return;
        }

        /**
         *[{"opr_id":"112024051413551038","resolv
         * e_time":"2024-05-14 10:08:27","status":"SUCCESS","data":[{"type":42,"value":["0.09|0.00|0.00"],"dsp":"总用量：0.09 m³  本月用量：0.00 m³ 阀门状态：关阀 表类型：远程预付费 购买次数：0 累计消费金额：0.00 元 剩
         * 金额:0.00 元 电池电压：3.6V 信号强度：-88"}]}
         */
        //double degree = contentObject.getJSONArray("data").getJSONObject(0).getJSONArray("value").getDouble(0);
        String value = contentObject.getJSONArray("data").getJSONObject(0).getJSONArray("value").getString(0);
        String[] values = value.split("\\|", 3);
        String degree = "0.0";
        if (values.length == 3) {
            degree = values[2];
        }


        smartMeterCoreReadImpl.saveMeterAndCreateFee(meterMachineDetailDtos.get(0), degree, degree, batchId);
    }

    @Override
    public void queryMeterMachineState(MeterMachineDto meterMachineDto) {
        List<Map<String, Object>> req = new ArrayList<>();

        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120104");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (meterMachineSpecDtos == null || meterMachineSpecDtos.size() < 1) {
            throw new CmdException(ResultVo.CODE_ERROR, "未配置采集器ID");
        }

        Map<String, Object> item = new HashMap<>();

        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        req.add(item);

        String request_content = JSON.toJSONString(req);

        String resStr = TdDianBiaoUtil.requestAsync(WATER_METER_QUERY_URL, request_content);

        JSONObject paramOut = JSONObject.parseObject(resStr);

        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            throw new CmdException(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }

        JSONArray responseContent = paramOut.getJSONArray("response_content");

        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if ("SUCCESS".equals(status)) {
                meterMachineDto.setState(MeterMachineDto.STATE_ONLINE);
                meterMachineDto.setStateName("在线");
            } else {
                throw new CmdException(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }

        meterMachineDto.setState(MeterMachineDto.STATE_OFFLINE);
        meterMachineDto.setStateName("离线");
    }


    @Override
    public ResultVo switchControl(MeterMachineDto meterMachineDto, String controlType) {

        List<Map<String, Object>> req = new ArrayList<>();
        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120104");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (meterMachineSpecDtos == null || meterMachineSpecDtos.size() < 1) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }
        String detailId = GenerateCodeFactory.getGeneratorId("11");
        Map<String, Object> item = new HashMap<>();
        item.put("opr_id", detailId);
        item.put("time_out", 0);
        item.put("must_online", true);
        item.put("retry_times", 1);
        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        item.put("address", meterMachineDto.getAddress());
        if ("ON".equals(controlType)) {
            item.put("type", 10);
        } else if ("OFF".equals(controlType)) {
            item.put("type", 11);
        }

        req.add(item);

        String request_content = JSON.toJSONString(req);

        String resStr = TdDianBiaoUtil.requestAsync(ELE_CONTROL_URL, request_content, UrlCache.getOwnerUrl() + ELE_CONTROL_NOTIFY_URL);

        JSONObject paramOut = JSONObject.parseObject(resStr);

        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            return new ResultVo(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }

        JSONArray responseContent = paramOut.getJSONArray("response_content");

        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if (!"SUCCESS".equals(status)) {
                return new ResultVo(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }

        MeterMachineDetailPo meterMachineDetailPo = new MeterMachineDetailPo();
        meterMachineDetailPo.setDetailId(detailId);
        meterMachineDetailPo.setMachineId(meterMachineDto.getMachineId());
        meterMachineDetailPo.setDetailType(controlType);
        meterMachineDetailPo.setPrestoreDegrees(meterMachineDto.getPrestoreDegrees());
        meterMachineDetailPo.setCurDegrees(meterMachineDto.getCurDegrees());
        meterMachineDetailPo.setCurReadingTime(meterMachineDto.getCurReadingTime());
        meterMachineDetailPo.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineDetailPo.setRemark("拉合闸");
        meterMachineDetailPo.setState(MeterMachineDetailDto.STATE_W);
        meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetail(meterMachineDetailPo);

        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电表反馈");
    }

    @Override
    public ResultVo cleanControl(MeterMachineDto meterMachineDto) {
        List<Map<String, Object>> req = new ArrayList<>();
        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120104");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (meterMachineSpecDtos == null || meterMachineSpecDtos.size() < 1) {
            return new ResultVo(ResultVo.CODE_ERROR, "未配置采集器ID");
        }
        //执行清零
        String detailId = GenerateCodeFactory.getGeneratorId("11");
        Map<String, Object> item = new HashMap<>();
        item.put("opr_id", detailId);
        item.put("time_out", 0);
        item.put("must_online", true);
        item.put("retry_times", 1);
        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        item.put("address", meterMachineDto.getAddress());
        req.add(item);
        String request_content = JSON.toJSONString(req);
        String resStr = TdDianBiaoUtil.requestAsync(CLEAN_URL, request_content, UrlCache.getOwnerUrl() + CLEAN_NOTIFY_URL);
        JSONObject paramOut = JSONObject.parseObject(resStr);
        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            return new ResultVo(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }
        JSONArray responseContent = paramOut.getJSONArray("response_content");
        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if (!"SUCCESS".equals(status)) {
                return new ResultVo(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }
        //暂停3秒执行开户
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        detailId = GenerateCodeFactory.getGeneratorId("11");
        item = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("money", "0");
        params.put("account_id", 1);
        params.put("count", 1);
        item.put("opr_id", detailId);
        item.put("time_out", 0);
        item.put("must_online", true);
        item.put("retry_times", 1);
        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        item.put("address", meterMachineDto.getAddress());
        item.put("params", params);
        req.add(item);
        request_content = JSON.toJSONString(req);
        resStr = TdDianBiaoUtil.requestAsync(OPEN_ACOUNT_URL, request_content, UrlCache.getOwnerUrl() + CLEAN_NOTIFY_URL);
        //{"response_content":"[{\"opr_id\":\"112024051033555817\",\"status\":\"ACCEPTED\"},{\"status\":\"FAIL\",\"error_msg\":\"\\u672a\\u7ed1\\u5b9a\\u7528\\u6237\",\"opr_id\"
        //:\"112024051008655818\"}]","status":"SUCCESS","timestamp":1715346684,"sign":"049718f2434bf088f845c7118e703a2a"}
        paramOut = JSONObject.parseObject(resStr);
        if (!"SUCCESS".equals(paramOut.getString("status"))) {
            return new ResultVo(ResultVo.CODE_ERROR, paramOut.getString("error_msg"));
        }
        responseContent = paramOut.getJSONArray("response_content");
        for (int resIndex = 0; resIndex < responseContent.size(); resIndex++) {
            String status = responseContent.getJSONObject(resIndex).getString("status");
            if ("FAIL".equals(status)) {
                return new ResultVo(ResultVo.CODE_ERROR, responseContent.getJSONObject(resIndex).getString("error_msg"));
            }
        }
        return new ResultVo(ResultVo.CODE_OK, "请求已发送，等待电表反馈");
    }
}
