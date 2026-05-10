package com.java110.meter.factory.fstshuibiao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.dto.meter.MeterMachineDto;
import com.java110.intf.meter.*;
import com.java110.meter.factory.ISmartMeterFactoryAdapt;
import com.java110.meter.factory.ISmartMeterCoreRead;
import com.java110.po.meter.MeterMachineDetailPo;
import com.java110.po.meter.MeterMachinePo;
import com.java110.po.meterMachineCharge.MeterMachineChargePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.java110.dto.meter.MeterMachineSpecDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service("fstElecMeterApiAdaptImpl")
@SuppressWarnings({"unchecked", "rawtypes"})
public class FstElecMeterApiAdaptImpl implements ISmartMeterFactoryAdapt {

    private static final Logger logger = LoggerFactory.getLogger(FstElecMeterApiAdaptImpl.class);

    private static final ThreadLocal<SimpleDateFormat> LOCAL_DATE_FORMAT = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );

    private static final String FIELD_SEPARATOR = "|";
    private static final String KEY_VALUE_SEPARATOR = ":";

    @Autowired
    private IMeterMachineV1InnerServiceSMO meterMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineDetailV1InnerServiceSMO meterMachineDetailV1InnerServiceSMOImpl;

    @Autowired
    private IMeterMachineChargeV1InnerServiceSMO meterMachineChargeV1InnerServiceSMOImpl;

    @Autowired
    private ISmartMeterCoreRead smartMeterCoreReadImpl;

    @Autowired
    private IMeterMachineSpecV1InnerServiceSMO meterMachineSpecV1InnerServiceSMOImpl;

    /**
     * 检查配置是否可用
     */
    private boolean isFstConfigAvailable() {
        try {
            return FstMeterUtil.isConfigAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将秒级时间戳转换为本地格式时间字符串
     */
    private String timestampToLocalString(int timestamp) {
        try {
            long timestampMillis = (long) timestamp * 1000L;
            Date date = new Date(timestampMillis);
            return LOCAL_DATE_FORMAT.get().format(date);
        } catch (Exception e) {
            logger.error("时间戳转换失败: {}", timestamp, e);
            return null;
        }
    }

    /**
     * 1. 请求单用户表数据 - V2.03版本（使用elecuseritem/getSingleElecuserData）
     */
    @Override
    public ResultVo requestRead(MeterMachineDto meterMachineDto) {
        meterMachineDto.setControlType("C");
        if (!isFstConfigAvailable()) {
            String errorMsg = FstMeterUtil.getConfigErrorMessage();
            meterMachineDto.setState("NONE");
            meterMachineDto.setStateName(errorMsg);
            return new ResultVo(ResultVo.CODE_ERROR,
                    "FST平台配置未就绪：" + (errorMsg != null ? errorMsg : "未知错误"));
        }

        try {
            logger.info("实时读取电表数据，表号：{}", meterMachineDto.getAddress());

            // 先获取用户编号
            String usernum = getUsernumFromLocalConfig(meterMachineDto);
            if (usernum == null || usernum.trim().isEmpty()) {
                meterMachineDto.setState("NONE");
                meterMachineDto.setStateName("未配置用户编号");
                return new ResultVo(ResultVo.CODE_ERROR, "未配置用户编号（usernum）");
            }

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("usernum", usernum);

            String url = FstMeterUtil.buildApiUrl("elecuseritem/getSingleElecuserData");
            String requestJson = FstMeterUtil.buildRequestJson(requestData);

            logger.debug("请求URL: {}", url);
            String responseStr = FstMeterUtil.requestSync(url, requestJson);

            if (responseStr == null || responseStr.trim().isEmpty()) {
                meterMachineDto.setState("NONE");
                meterMachineDto.setStateName("平台无响应");
                return new ResultVo(ResultVo.CODE_ERROR, "平台无响应，请检查网络连接");
            }

            logger.debug("响应数据: {}", responseStr);
            Map<String, Object> response = FstMeterUtil.parseResponse(responseStr);

            if (response == null) {
                meterMachineDto.setState("NONE");
                meterMachineDto.setStateName("响应解析失败");
                return new ResultVo(ResultVo.CODE_ERROR, "响应解析失败");
            }

            // 获取returninfo部分
            Map<String, Object> returnInfo = null;
            Object returnInfoObj = response.get("returninfo");
            if (returnInfoObj instanceof Map) {
                returnInfo = (Map<String, Object>) returnInfoObj;
            }

            if (returnInfo == null) {
                meterMachineDto.setState("NONE");
                meterMachineDto.setStateName("响应格式错误");
                return new ResultVo(ResultVo.CODE_ERROR, "响应格式错误");
            }

            // 获取状态码和状态信息
            String status = (String) returnInfo.get("status");
            String message = (String) returnInfo.get("message");

            if ("success".equals(status)) {
                // ✅ 先检查数据中的meterid是否匹配
                Object dataObj = returnInfo.get("data");
                if (dataObj != null && !".".equals(dataObj.toString())) {
                    try {
                        JSONObject data = parseDataObject(dataObj);
                        if (data != null) {
                            String meterId = data.getString("meterid");
                            // ✅ 在这里进行表号比较
                            if (meterId != null && !meterId.equals(meterMachineDto.getAddress())) {
                                meterMachineDto.setState("NONE"); // 设置状态为NONE
                                meterMachineDto.setStateName("该用户绑定电表号不符");
                                String errorMsg = String.format("用户绑定电表号不符（本地：%s，平台：%s）",
                                        meterMachineDto.getAddress(), meterId);
                                logger.warn(errorMsg);
                                return new ResultVo(ResultVo.CODE_ERROR, errorMsg);
                            }

                            // 如果表号匹配，再继续处理
                            String valveStatus = data.getString("valvestatus");
                            meterMachineDto.setState(MeterMachineDto.STATE_ONLINE);

                            String changevalue = "闸开".equals(valveStatus) ? "OFF" : "ON";
                            meterMachineDto.setControlType(changevalue);

                            // 处理表具数据，传入false表示不写入抄表记录
                            processMeterDataResponse(response, meterMachineDto, false);

                            // 设置stateName
                            if (valveStatus != null && !valveStatus.trim().isEmpty()) {
                                meterMachineDto.setStateName(valveStatus);
                            } else if (message != null && !message.trim().isEmpty()) {
                                meterMachineDto.setStateName(message);
                            } else {
                                meterMachineDto.setStateName("在线");
                            }

                            logger.info("电表状态查询成功，表号：{}，阀门状态：{}",
                                    meterMachineDto.getAddress(), meterMachineDto.getStateName());
                            return new ResultVo(ResultVo.CODE_OK, "电表状态查询成功：" + meterMachineDto.getStateName());
                        }
                    } catch (Exception e) {
                        logger.warn("解析数据失败", e);
                        meterMachineDto.setState("NONE");
                        meterMachineDto.setStateName("数据解析异常");
                        return new ResultVo(ResultVo.CODE_ERROR, "数据解析异常：" + e.getMessage());
                    }
                }

                // 如果到这里还没有返回，说明数据为空或无meterid
                meterMachineDto.setState("NONE");
                meterMachineDto.setStateName("无有效数据");
                return new ResultVo(ResultVo.CODE_ERROR, "平台返回无有效数据");

            } else if ("error".equals(status) || "fail".equals(status)) {
                // status="error"时：state="NONE", stateName=message
                meterMachineDto.setState("NONE");

                if (message != null && !message.trim().isEmpty()) {
                    meterMachineDto.setStateName(message);
                } else {
                    meterMachineDto.setStateName("接口错误");
                }

                logger.info("电表状态查询返回错误，表号：{}，错误信息：{}",
                        meterMachineDto.getAddress(), meterMachineDto.getStateName());

                return new ResultVo(ResultVo.CODE_ERROR, "电表状态查询失败：" + meterMachineDto.getStateName());

            } else {
                // 其他情况：state="NONE"
                meterMachineDto.setState("NONE");

                if (message != null && !message.trim().isEmpty()) {
                    meterMachineDto.setStateName(message);
                } else {
                    meterMachineDto.setStateName("未知状态");
                }

                logger.warn("电表状态查询异常，表号：{}，状态：{}，信息：{}",
                        meterMachineDto.getAddress(), status, meterMachineDto.getStateName());
                return new ResultVo(ResultVo.CODE_ERROR, "读取异常：" + meterMachineDto.getStateName());
            }

        } catch (Exception e) {
            logger.error("读取电表数据异常", e);
            meterMachineDto.setState("NONE");
            meterMachineDto.setStateName("读取异常：" + e.getMessage());
            return new ResultVo(ResultVo.CODE_ERROR, "读取异常：" + e.getMessage());
        }
    }

    /**
     * 处理电表数据响应
     * @param writeDetail 是否写入抄表记录
     */
    private ResultVo processMeterDataResponse(Map<String, Object> response, MeterMachineDto meterMachineDto, boolean writeDetail) {
        Map<String, Object> returnInfo = null;
        Object returnInfoObj = response.get("returninfo");
        if (returnInfoObj instanceof Map) {
            returnInfo = (Map<String, Object>) returnInfoObj;
        }

        Object dataObj = returnInfo != null ? returnInfo.get("data") : null;

        if (dataObj != null && !".".equals(dataObj.toString())) {
            try {
                JSONObject data = parseDataObject(dataObj);
                if (data != null) {
                    String accountbalances = data.getString("accountbalances");  // 用户余额（元）
                    String valveStatus = data.getString("valvestatus");
                    String totalUse = data.getString("totaluse");
                    String meterId = data.getString("meterid");
                    String username = data.getString("username");
                    int metertimeInt = data.getIntValue("metertime");
                    String usernum = data.getString("usernum");

                    // ✅ 简单比较：如果平台返回的meterId与本地表号不一致
                    if (meterId != null && !meterId.equals(meterMachineDto.getAddress())) {
                        meterMachineDto.setStateName("表号不存在");
                        logger.warn("电表号不符！本地表号：{}，平台表号：{}",
                                meterMachineDto.getAddress(), meterId);
                        return new ResultVo(ResultVo.CODE_ERROR, "用户绑定电表号不符");
                    }

                    // 转换时间戳
                    String metertime = timestampToLocalString(metertimeInt);
                    if (metertime == null) {
                        metertime = timestampToLocalString((int)(System.currentTimeMillis() / 1000));
                    }

                    // 处理表具数据，传入writeDetail参数控制是否写入抄表记录
                    ResultVo processResult = processMeterData(meterMachineDto, totalUse, accountbalances,
                            metertime, valveStatus, usernum, writeDetail);

                    if (processResult.getCode() == ResultVo.CODE_OK) {
                        StringBuilder resultMsg = new StringBuilder("读取成功");
                        if (meterId != null) resultMsg.append("，表号：").append(meterId);
                        if (totalUse != null) resultMsg.append("，总用量：").append(totalUse).append("吨");
                        if (accountbalances != null) resultMsg.append("，用户余额：").append(accountbalances).append("元");
                        if (valveStatus != null) resultMsg.append("，阀门状态：").append(valveStatus);
                        if (username != null) resultMsg.append("，用户：").append(username);
                        if (metertime != null) resultMsg.append("，表具时间：").append(metertime);
                        if (usernum != null) resultMsg.append("，用户编号：").append(usernum);

                        return new ResultVo(ResultVo.CODE_OK, resultMsg.toString());
                    } else {
                        return processResult;
                    }
                }
            } catch (Exception e) {
                logger.error("解析响应数据失败", e);
                return new ResultVo(ResultVo.CODE_OK, "读取成功，但解析数据失败");
            }
        }
        return new ResultVo(ResultVo.CODE_OK, "请求成功，无详细数据");
    }

    /**
     * 解析数据对象
     */
    private JSONObject parseDataObject(Object dataObj) {
        if (dataObj instanceof JSONObject) {
            return (JSONObject) dataObj;
        } else if (dataObj instanceof Map) {
            return new JSONObject((Map) dataObj);
        } else if (dataObj instanceof String) {
            String dataStr = (String) dataObj;
            if (!dataStr.trim().isEmpty() && !".".equals(dataStr)) {
                return JSON.parseObject(dataStr);
            }
        }
        return null;
    }

    /**
     * 处理表具数据
     * @param writeDetail 是否写入抄表记录
     */
    private ResultVo processMeterData(MeterMachineDto meterMachineDto, String totalUse,
                                      String accountbalances, String metertime, String valveStatus,
                                      String usernum, boolean writeDetail) {
        try {
            if (metertime == null || metertime.trim().isEmpty()) {
                metertime = timestampToLocalString((int)(System.currentTimeMillis() / 1000));
            }

            // 处理null值
            if (accountbalances == null) {
                accountbalances = "0";
                logger.warn("处理表具数据：accountbalances为null，使用默认值'0'，表号：{}", meterMachineDto.getAddress());
            }

            if (totalUse == null) {
                totalUse = "0";
            }

            // 1. 更新meter_machine表
            updateMeterMachineTable(meterMachineDto, totalUse, accountbalances, metertime);

            // 2. 只在writeDetail为true时保存抄表详情记录
            if (writeDetail) {
                saveMeterDetail(meterMachineDto, totalUse, accountbalances, metertime, valveStatus, usernum);
            } else {
                logger.info("获取表列表，不写入抄表记录，表具ID：{}", meterMachineDto.getMachineId());
            }

            return new ResultVo(ResultVo.CODE_OK, "数据处理成功");

        } catch (Exception e) {
            logger.error("处理表具数据异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, "处理数据异常：" + e.getMessage());
        }
    }

    /**
     * 更新meter_machine表
     * 修改：curDegrees存储用户余额（accountbalances）
     */
    private void updateMeterMachineTable(MeterMachineDto meterMachineDto, String totalUse,
                                         String accountbalances, String metertime) {
        try {
            MeterMachinePo meterMachinePo = new MeterMachinePo();
            meterMachinePo.setMachineId(meterMachineDto.getMachineId());
            meterMachinePo.setCommunityId(meterMachineDto.getCommunityId());
            meterMachinePo.setCurDegrees(accountbalances != null ? accountbalances : "0");  // 存储用户余额
            meterMachinePo.setPrestoreDegrees(totalUse != null ? totalUse : "0");  // 存储总用量
            meterMachinePo.setCurReadingTime(metertime);

            int result = meterMachineV1InnerServiceSMOImpl.updateMeterMachine(meterMachinePo);

            logger.info("更新meter_machine表结果: {}，表具ID：{}", result, meterMachineDto.getMachineId());
        } catch (Exception e) {
            logger.error("更新meter_machine表异常", e);
        }
    }

    /**
     * 保存抄表详情记录（仅在充值时调用）
     */
    private void saveMeterDetail(MeterMachineDto meterMachineDto, String totalUse,
                                 String accountbalances, String metertime, String valveStatus,
                                 String usernum) {
        try {
            StringBuilder remarkBuilder = new StringBuilder("FST实时获取数据");
            if (valveStatus != null) {
                remarkBuilder.append("，阀门状态:").append(valveStatus);
            }
            if (accountbalances != null && !accountbalances.isEmpty()) {
                remarkBuilder.append("，用户余额:").append(accountbalances).append("元");
            }
            if (usernum != null && !usernum.isEmpty()) {
                remarkBuilder.append("，用户编号:").append(usernum);
            }

            MeterMachineDetailPo detailPo = new MeterMachineDetailPo();
            detailPo.setDetailId(GenerateCodeFactory.getGeneratorId("11"));
            detailPo.setMachineId(meterMachineDto.getMachineId());
            detailPo.setCommunityId(meterMachineDto.getCommunityId());
            detailPo.setDetailType("2002");
            detailPo.setCurDegrees(accountbalances != null ? accountbalances : "0");  // 存储用户余额
            detailPo.setCurReadingTime(metertime);
            detailPo.setPrestoreDegrees(totalUse != null ? totalUse : "0");  // 存储总用量
            detailPo.setState("C");
            detailPo.setRemark(remarkBuilder.toString());

            int result = meterMachineDetailV1InnerServiceSMOImpl.saveMeterMachineDetail(detailPo);

            if (result > 0) {
                logger.info("保存抄表详情成功，详情ID：{}，表具ID：{}", detailPo.getDetailId(), meterMachineDto.getMachineId());
            }
        } catch (Exception e) {
            logger.error("保存抄表详情记录异常", e);
        }
    }

    /**
     * 2. 批量读取电表数据 - V2.04版本
     */
    @Override
    public ResultVo requestReads(List<MeterMachineDto> meterMachineDtos) {
        if (!isFstConfigAvailable()) {
            return new ResultVo(ResultVo.CODE_ERROR, "FST平台配置未就绪，请检查系统配置");
        }

        if (meterMachineDtos == null || meterMachineDtos.isEmpty()) {
            return new ResultVo(ResultVo.CODE_ERROR, "电表列表为空");
        }

        logger.info("开始批量读取电表数据，共 {} 个电表", meterMachineDtos.size());

        int successCount = 0;
        int failCount = 0;
        StringBuilder resultMsg = new StringBuilder();

        for (MeterMachineDto meter : meterMachineDtos) {
            ResultVo singleResult = requestRead(meter);
            if (singleResult.getCode() == ResultVo.CODE_OK) {
                successCount++;
            } else {
                failCount++;
                resultMsg.append("表具").append(meter.getAddress()).append("读取失败; ");
            }
        }

        String finalMsg = String.format("批量读取完成：成功%d个，失败%d个", successCount, failCount);
        if (failCount > 0) {
            finalMsg += "，失败详情：" + resultMsg.toString();
            return new ResultVo(ResultVo.CODE_ERROR, finalMsg);
        } else {
            return new ResultVo(ResultVo.CODE_OK, finalMsg);
        }
    }

    /**
     * 3. 创建充值订单并立即缴费 - V2.03版本
     */
    @Override
    public ResultVo requestRecharge(MeterMachineDto meterMachineDto, double degree, double money) {
        if (!isFstConfigAvailable()) {
            return new ResultVo(ResultVo.CODE_ERROR, "FST平台配置未就绪，请检查系统配置");
        }

        try {
            // 充值前先读取最新数据（用户余额），但不写入抄表记录
            String preAccountBalances = readUserDataBeforeRecharge(meterMachineDto);
            if (preAccountBalances == null) {
                return new ResultVo(ResultVo.CODE_ERROR, "充值前读取用户数据失败");
            }

            // 从本地配置获取用户编号（usernum）
            String usernum = getUsernumFromLocalConfig(meterMachineDto);
            if (usernum == null || usernum.trim().isEmpty()) {
                return new ResultVo(ResultVo.CODE_ERROR, "未配置用户编号（usernum）");
            }

            int amountInFen = (int)(money * 100);
            String chargeId = GenerateCodeFactory.getGeneratorId("11");

            // 创建订单
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("usernum", usernum);
            requestData.put("meterid", meterMachineDto.getAddress());
            requestData.put("amount", amountInFen);
            requestData.put("payment", "电费");

            String url = FstMeterUtil.buildApiUrl("elecchargeitem/createOrder");
            String requestJson = FstMeterUtil.buildRequestJson(requestData);
            String responseStr = FstMeterUtil.requestSync(url, requestJson);

            if (responseStr == null || responseStr.trim().isEmpty()) {
                return new ResultVo(ResultVo.CODE_ERROR, "平台无响应，请检查网络连接");
            }

            Map<String, Object> response = FstMeterUtil.parseResponse(responseStr);

            if (FstMeterUtil.isSuccessResponse(response)) {
                Map<String, Object> returnInfo = (Map<String, Object>) response.get("returninfo");
                Object dataObj = returnInfo != null ? returnInfo.get("data") : null;

                if (dataObj != null && !".".equals(dataObj.toString())) {
                    JSONObject data = parseDataObject(dataObj);
                    if (data != null) {
                        String orderId = extractOrderId(data);
                        double chargeFlow = extractChargeFlow(data);

                        if (orderId != null) {
                            // 执行缴费
                            return orderChargeImmediately(orderId, amountInFen, meterMachineDto,
                                    chargeId, money, chargeFlow, preAccountBalances);
                        }
                    }
                }
                return new ResultVo(ResultVo.CODE_ERROR, "创建订单成功，但无法获取订单详情");
            } else {
                String errorMsg = FstMeterUtil.getErrorMessage(response);
                return new ResultVo(ResultVo.CODE_ERROR, "创建订单失败：" + errorMsg);
            }
        } catch (Exception e) {
            logger.error("创建充值订单异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, "创建订单异常：" + e.getMessage());
        }
    }

    /**
     * 从本地配置获取用户编号
     */
    private String getUsernumFromLocalConfig(MeterMachineDto meterMachineDto) {
        try {
            MeterMachineSpecDto meterMachineSpecDto = new MeterMachineSpecDto();
            meterMachineSpecDto.setMachineId(meterMachineDto.getMachineId());
            meterMachineSpecDto.setCommunityId(meterMachineDto.getCommunityId());
            meterMachineSpecDto.setSpecId("120106"); // 用户编号配置

            List<MeterMachineSpecDto> meterMachineSpecDtos =
                    meterMachineSpecV1InnerServiceSMOImpl.queryMeterMachineSpecs(meterMachineSpecDto);

            if (meterMachineSpecDtos == null || meterMachineSpecDtos.isEmpty()) {
                logger.warn("未配置用户编号（usernum），表具ID：{}", meterMachineDto.getMachineId());
                return null;
            }

            // 返回第一个配置的值
            return meterMachineSpecDtos.get(0).getSpecValue();
        } catch (Exception e) {
            logger.error("获取本地用户编号配置异常", e);
            return null;
        }
    }

    /**
     * 充值前读取用户数据（不写入抄表记录）- 使用elecuseritem/getSingleElecuserData
     * @return 充值前的用户余额，如果读取失败返回null
     */
    private String readUserDataBeforeRecharge(MeterMachineDto meterMachineDto) {
        try {
            logger.info("充值前读取用户数据，表号：{}", meterMachineDto.getAddress());

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("usernum", getUsernumFromLocalConfig(meterMachineDto));

            String url = FstMeterUtil.buildApiUrl("elecuseritem/getSingleElecuserData");
            String requestJson = FstMeterUtil.buildRequestJson(requestData);
            String responseStr = FstMeterUtil.requestSync(url, requestJson);

            if (responseStr == null || responseStr.trim().isEmpty()) {
                logger.error("充值前读取数据失败：平台无响应");
                return null;
            }

            Map<String, Object> response = FstMeterUtil.parseResponse(responseStr);

            if (FstMeterUtil.isSuccessResponse(response)) {
                Map<String, Object> returnInfo = (Map<String, Object>) response.get("returninfo");
                Object dataObj = returnInfo != null ? returnInfo.get("data") : null;

                if (dataObj != null && !".".equals(dataObj.toString())) {
                    JSONObject data = parseDataObject(dataObj);
                    if (data != null) {
                        // 获取用户余额，如果为null则使用"0"
                        String accountbalances = data.getString("accountbalances");
                        if (accountbalances == null) {
                            logger.warn("充值前读取数据：accountbalances为null，使用默认值'0'");
                            accountbalances = "0";
                        }

                        // 记录读取到的数据
                        logger.info("充值前成功获取用户数据，表号：{}，用户余额：{}元，总用量：{}，阀门状态：{}，用户编号：{}",
                                meterMachineDto.getAddress(),
                                accountbalances,
                                data.getString("totaluse"),
                                data.getString("valvestatus"),
                                data.getString("usernum"));
                        return accountbalances;
                    }
                }
                logger.error("充值前读取数据失败：无法获取用户数据");
                return null;
            } else {
                String errorMsg = FstMeterUtil.getErrorMessage(response);
                logger.error("充值前读取数据失败：{}", errorMsg);
                return null;
            }
        } catch (Exception e) {
            logger.error("充值前读取用户数据异常", e);
            return null;
        }
    }

    /**
     * 提取订单ID
     */
    private String extractOrderId(JSONObject data) {
        if (data.containsKey("data")) {
            JSONObject innerData = data.getJSONObject("data");
            if (innerData != null) {
                return innerData.getString("orderid");
            }
        }
        return data.getString("orderid");
    }

    /**
     * 提取充值电量
     */
    private double extractChargeFlow(JSONObject data) {
        if (data.containsKey("data")) {
            JSONObject innerData = data.getJSONObject("data");
            if (innerData != null) {
                return innerData.getDoubleValue("chargeflow");
            }
        }
        return data.getDoubleValue("chargeflow");
    }

    /**
     * 立即执行订单缴费
     * @param preAccountBalances 充值前的用户余额
     */
    private ResultVo orderChargeImmediately(String orderId, int chargeMoney, MeterMachineDto meterMachineDto,
                                            String chargeId, double money, double chargeFlow, String preAccountBalances) {
        try {
            String bankSerialNum = "BANK_" + System.currentTimeMillis() + "_" + orderId;

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("orderid", orderId);
            requestData.put("chargemoney", chargeMoney);
            requestData.put("amount", chargeMoney);
            requestData.put("paytime", System.currentTimeMillis() / 1000);
            requestData.put("bankserialnum", bankSerialNum);

            String url = FstMeterUtil.buildApiUrl("elecchargeitem/orderCharge");
            String requestJson = FstMeterUtil.buildRequestJson(requestData);
            String responseStr = FstMeterUtil.requestSync(url, requestJson);

            if (responseStr == null) {
                return new ResultVo(ResultVo.CODE_ERROR, "缴费失败：HTTP响应为空");
            }

            Map<String, Object> response = FstMeterUtil.parseResponse(responseStr.trim());

            if (FstMeterUtil.isSuccessResponse(response)) {
                // 充值成功后同步最新用户数据，并写入抄表记录（传入true）
                String afterAccountBalances = syncUserDataAfterRecharge(meterMachineDto);

                if (afterAccountBalances == null) {
                    logger.warn("充值后同步数据失败");
                }

                // 保存充值记录
                saveChargeRecord(meterMachineDto, chargeId, money, chargeFlow, "SUCCESS",
                        "缴费成功", orderId, bankSerialNum, preAccountBalances, afterAccountBalances);

                return new ResultVo(ResultVo.CODE_OK, "充值成功！订单号：" + orderId +
                        "，充值电量：" + String.format("%.3f", chargeFlow) + "度，金额：" + money + "元");
            } else {
                String errorMsg = FstMeterUtil.getErrorMessage(response);
                return new ResultVo(ResultVo.CODE_ERROR, "缴费失败：" + errorMsg);
            }
        } catch (Exception e) {
            logger.error("订单缴费异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, "缴费异常：" + e.getMessage());
        }
    }

    /**
     * 充值后同步用户数据（写入抄表记录）- 使用elecuseritem/getSingleElecuserData
     * @return 充值后的用户余额，如果同步失败返回null
     */
    private String syncUserDataAfterRecharge(MeterMachineDto meterMachineDto) {
        try {
            logger.info("充值后同步用户数据，表号：{}", meterMachineDto.getAddress());

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("usernum", getUsernumFromLocalConfig(meterMachineDto));

            String url = FstMeterUtil.buildApiUrl("elecuseritem/getSingleElecuserData");
            String requestJson = FstMeterUtil.buildRequestJson(requestData);
            String responseStr = FstMeterUtil.requestSync(url, requestJson);

            if (responseStr == null || responseStr.trim().isEmpty()) {
                logger.error("充值后同步数据失败：平台无响应");
                return null;
            }

            Map<String, Object> response = FstMeterUtil.parseResponse(responseStr);

            if (FstMeterUtil.isSuccessResponse(response)) {
                Map<String, Object> returnInfo = (Map<String, Object>) response.get("returninfo");
                Object dataObj = returnInfo != null ? returnInfo.get("data") : null;

                if (dataObj != null && !".".equals(dataObj.toString())) {
                    JSONObject data = parseDataObject(dataObj);
                    if (data != null) {
                        // 获取用户余额
                        String accountbalances = data.getString("accountbalances");

                        // 处理表具数据，传入true表示写入抄表记录
                        ResultVo processResult = processMeterDataResponse(response, meterMachineDto, false);

                        if (processResult.getCode() == ResultVo.CODE_OK && accountbalances != null) {
                            logger.info("充值后同步用户数据成功，用户余额：{}元", accountbalances);
                            return accountbalances;
                        }
                    }
                }
                logger.error("充值后同步数据失败：无法获取用户数据");
                return null;
            } else {
                String errorMsg = FstMeterUtil.getErrorMessage(response);
                logger.error("充值后同步数据失败：{}", errorMsg);
                return null;
            }
        } catch (Exception e) {
            logger.error("充值后同步用户数据异常", e);
            return null;
        }
    }

    /**
     * 保存充值记录
     * @param preAccountBalances 充值前的用户余额
     * @param afterAccountBalances 充值后的用户余额
     */
    private void saveChargeRecord(MeterMachineDto meterMachineDto, String chargeId,
                                  double money, double chargeFlow, String vendorStatus,
                                  String statusName, String orderId, String bankSerialNum,
                                  String preAccountBalances, String afterAccountBalances) {
        try {
            MeterMachineChargePo chargePo = new MeterMachineChargePo();
            chargePo.setChargeId(chargeId);
            chargePo.setMachineId(meterMachineDto.getMachineId());
            chargePo.setCommunityId(meterMachineDto.getCommunityId());
            chargePo.setChargeMoney(String.valueOf(money));
            chargePo.setChargeDegrees(String.valueOf(chargeFlow));
            chargePo.setState("SUCCESS".equals(vendorStatus) ? "C" : "F");

            String remark = buildStructuredRemark(statusName, vendorStatus,
                    LOCAL_DATE_FORMAT.get().format(new Date()),
                    orderId, bankSerialNum, preAccountBalances, afterAccountBalances);
            chargePo.setRemark(remark);

            meterMachineChargeV1InnerServiceSMOImpl.saveMeterMachineCharge(chargePo);
        } catch (Exception e) {
            logger.error("保存充值记录异常", e);
        }
    }

    /**
     * 构建结构化备注信息
     * 状态名:缴费成功|状态码:SUCCESS|完成时间:2026-01-12 23:04:58|订单号:20310020260112230459791203|
     * 充值前余额:100.01元|充值后余额:200.01元|银行流水:BANK_1768230298616_20310020260112230459791203
     */
    private String buildStructuredRemark(String statusName, String vendorStatus, String resolveTime,
                                         String orderId, String bankSerialNum,
                                         String preAccountBalances, String afterAccountBalances) {
        StringBuilder remark = new StringBuilder();

        remark.append("状态名").append(KEY_VALUE_SEPARATOR).append(statusName != null ? statusName : "")
                .append(FIELD_SEPARATOR)
                .append("状态码").append(KEY_VALUE_SEPARATOR).append(vendorStatus != null ? vendorStatus : "");

        if (resolveTime != null) {
            remark.append(FIELD_SEPARATOR)
                    .append("完成时间").append(KEY_VALUE_SEPARATOR).append(resolveTime);
        }

        if (orderId != null) {
            remark.append(FIELD_SEPARATOR)
                    .append("订单号").append(KEY_VALUE_SEPARATOR).append(orderId);
        }

        // 添加充值前余额
        if (preAccountBalances != null) {
            remark.append(FIELD_SEPARATOR)
                    .append("充值前余额").append(KEY_VALUE_SEPARATOR).append(preAccountBalances).append("元");
        }

        // 添加充值后余额
        if (afterAccountBalances != null) {
            remark.append(FIELD_SEPARATOR)
                    .append("充值后余额").append(KEY_VALUE_SEPARATOR).append(afterAccountBalances).append("元");
        }

        if (bankSerialNum != null) {
            remark.append(FIELD_SEPARATOR)
                    .append("银行流水").append(KEY_VALUE_SEPARATOR).append(bankSerialNum);
        }

        return remark.toString();
    }

    /**
     * 6. 异步通知处理
     */
    @Override
    public ResultVo notifyReadData(String readData) {
        try {
            logger.info("FST水务平台异步通知数据: {}", readData);
            return new ResultVo(ResultVo.CODE_OK, "通知已接收");
        } catch (Exception e) {
            logger.error("处理异步通知异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, "处理通知异常");
        }
    }

    /**
     * 7. 查询表具状态
     */
    @Override
    public void queryMeterMachineState(MeterMachineDto meterMachineDto) {
        try {
            ResultVo result = requestRead(meterMachineDto);
            if (result.getCode() == ResultVo.CODE_OK) {
                meterMachineDto.setState(MeterMachineDto.STATE_ONLINE);
                meterMachineDto.setStateName("在线");
            } else {
                meterMachineDto.setState(MeterMachineDto.STATE_OFFLINE);
                meterMachineDto.setStateName("离线");
            }
        } catch (Exception e) {
            meterMachineDto.setState(MeterMachineDto.STATE_OFFLINE);
            meterMachineDto.setStateName("离线：" + e.getMessage());
        }
    }

    /**
     * 8. 开关闸控制
     */
    @Override
    public ResultVo switchControl(MeterMachineDto meterMachineDto, String controlType) {
        if (!isFstConfigAvailable()) {
            return new ResultVo(ResultVo.CODE_ERROR, "FST平台配置未就绪，请检查系统配置");
        }

        try {
            String changevalue = "ON".equals(controlType) ? "闸关" : "闸开";

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("meterid", meterMachineDto.getAddress());
            requestData.put("value", changevalue);

            String url = FstMeterUtil.buildApiUrl("meteritem/changeSingleMeterValve");
            String requestJson = FstMeterUtil.buildRequestJson(requestData);
            String responseStr = FstMeterUtil.requestSync(url, requestJson);

            Map<String, Object> response = FstMeterUtil.parseResponse(responseStr);

            if (FstMeterUtil.isSuccessResponse(response)) {
                return new ResultVo(ResultVo.CODE_OK, "开关闸控制成功，等待表端执行。");
            } else {
                String errorMsg = FstMeterUtil.getErrorMessage(response);
                return new ResultVo(ResultVo.CODE_ERROR, "开关闸控制失败：" + errorMsg);
            }
        } catch (Exception e) {
            logger.error("开关闸控制异常", e);
            return new ResultVo(ResultVo.CODE_ERROR, "开关闸控制异常：" + e.getMessage());
        }
    }

    /**
     * 9. 清零控制
     */
    @Override
    public ResultVo cleanControl(MeterMachineDto meterMachineDto) {
        return new ResultVo(ResultVo.CODE_ERROR, "清零功能暂未实现");
    }

    /**
     * 清理ThreadLocal资源
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            LOCAL_DATE_FORMAT.remove();
        } finally {
            super.finalize();
        }
    }
}