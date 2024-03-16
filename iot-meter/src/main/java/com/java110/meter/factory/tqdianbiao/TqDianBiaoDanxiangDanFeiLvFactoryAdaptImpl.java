package com.java110.meter.factory.tqdianbiao;

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
import com.java110.intf.meter.IMeterMachineDetailV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineSpecV1InnerServiceSMO;
import com.java110.intf.meter.IMeterMachineV1InnerServiceSMO;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.meter.factory.ISmartMeterCoreRead;
import com.java110.meter.factory.ISmartMeterFactoryAdapt;
import com.java110.po.meter.MeterMachineDetailPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
   拓强智能电表 处理类 -- 5.1 单项单费率抄表
   http://doc-api.tqdianbiao.com/#/api2/6/2/1
 */
@Service("tqDianBiaoDanxiangDanFeiLvFactoryAdaptImpl")
public class TqDianBiaoDanxiangDanFeiLvFactoryAdaptImpl implements ISmartMeterFactoryAdapt {
    private static final String READ_URL = "http://api2.tqdianbiao.com/Api_v2/ele_read";

    private static final String NOTIFY_URL = "/app/smartMeter/notify/a";

    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;


    @Autowired
    private IUserV1InnerServiceSMO userInnerServiceSMOImpl;

    private static final String ELE_METER_QUERY_URL = "http://api2.tqdianbiao.com/Api_v2/ele_meter/query";//电能表档案查询

    private static final String ELE_CONTROL_URL = "http://api2.tqdianbiao.com/Api_v2/ele_control";
    private static final String ELE_CONTROL_NOTIFY_URL = "/app/smartMeter/notify/f/switchControl";

    @Autowired
    private ISmartMeterCoreRead smartMeterCoreReadImpl;

    @Override
    public ResultVo requestRecharge(MeterMachineDto meterMachineDto, double degree,double money) {
        return new ResultVo(ResultVo.CODE_ERROR, "单项单费率 不支持充值");
    }

    @Override
    public ResultVo requestRead(MeterMachineDto meterMachineDto) {
        List<Map<String, Object>> req = new ArrayList<>();


        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120102");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (ListUtil.isNull(meterMachineSpecDtos)) {
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
        item.put("type", 3);
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
            meterMachineSpecDto.setSpecId("120101");
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
            item.put("type", 3);
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

        if (contentArray == null || contentArray.size() < 1) {
            return new ResultVo(ResultVo.CODE_ERROR, "没有数据");
        }

        MeterMachineDetailDto meterMachineDetailDto = new MeterMachineDetailDto();
        meterMachineDetailDto.setDetailId(contentArray.getJSONObject(0).getString("opr_id"));
        meterMachineDetailDto.setState(MeterMachineDetailDto.STATE_W);
        List<MeterMachineDetailDto> meterMachineDetailDtos = meterMachineDetailV1InnerServiceSMOImpl.queryMeterMachineDetails(meterMachineDetailDto);
        if (meterMachineDetailDtos == null || meterMachineDetailDtos.size() < 1) {
            return new ResultVo(ResultVo.CODE_ERROR, "没有数据");
        }

        String batchId = smartMeterCoreReadImpl.generatorBatch(meterMachineDetailDtos.get(0).getCommunityId());


        for (int i = 0; i < contentArray.size(); ++i) {
            try {
                JSONObject contentObject = contentArray.getJSONObject(i);
                doBusiness(contentObject, batchId);
            } catch (Exception e) {
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

        double degree = contentObject.getJSONArray("data").getJSONObject(0).getJSONArray("value").getDouble(0);

        smartMeterCoreReadImpl.saveMeterAndCreateFee(meterMachineDetailDtos.get(0), degree + "","", batchId);
    }

    @Override
    public void queryMeterMachineState(MeterMachineDto meterMachineDto) {
        List<Map<String, Object>> req = new ArrayList<>();

        MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
        meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
        meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
        meterMachineSpecDto.setSpecId("120102");
        List<MeterMachineSpecDto> meterMachineSpecDtos = meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);
        if (meterMachineSpecDtos == null || meterMachineSpecDtos.size() < 1) {
            throw new CmdException(ResultVo.CODE_ERROR, "未配置采集器ID");
        }

        Map<String, Object> item = new HashMap<>();

        item.put("cid", meterMachineSpecDtos.get(0).getSpecValue());
        req.add(item);

        String request_content = JSON.toJSONString(req);

        String resStr = TdDianBiaoUtil.requestAsync(ELE_METER_QUERY_URL, request_content);

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
        meterMachineSpecDto.setSpecId("120102");
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
        return null;
    }


}
