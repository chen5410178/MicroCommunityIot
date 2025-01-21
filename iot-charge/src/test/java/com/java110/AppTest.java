package com.java110;

import com.alibaba.fastjson.JSONObject;
import com.java110.charge.factory.lvcc.LvCCUtil;
import com.java110.charge.factory.yuncarcharge.YunCarUtil;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.BytesUtil;
import com.java110.core.utils.ListUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.chargeMachine.ChargeMachineDto;
import com.java110.dto.chargeMachine.ChargeMachineOrderDto;
import com.java110.dto.chargeMachine.ChargeMachinePortDto;
import com.java110.po.chargeProcessAnalysis.ChargeProcessAnalysisPo;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp(){
        String dataHex = "3839383630343938313032324330343038353530";
        // todo 结束原因
        String bb = null;
        try {
            bb = new String(BytesUtil.hexStringToByteArray(dataHex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(bb);
    }

    public void testByte(){

        String dataHex = "684000190013662024110100050120250121190926586620241101000501030201180e9e01380000000000000000354502003b00280f0000280f0000100e00000000d46e";

        byte[] data = BytesUtil.hexStringToByteArray(dataHex);
        int prexPos = 0; // 起始标志
        int prexLen = 2;
        int dataLenPos = prexLen; //数据长度
        int dataLen = 2;
        int seqPos = dataLenPos + 2; //序列号域
        int seqLen = 4;
        int secureFlagPos = seqPos + seqLen;//加密标志
        int secureFlagLen = 2;
        int cmdPos = secureFlagPos + 2;
        int cmdLen = 2;
        int transPos = cmdPos + cmdLen;
        int transLen = 32;
        int machineCodePos = transPos + transLen;
        int machineCodeLen = 14;
        int portCodePos = machineCodePos + machineCodeLen;
        int portCodeLen = 2;
        int statePos = portCodePos + portCodeLen;
        int stateLen = 2;
        int backInPlacePos = statePos + stateLen;
        int backInPlaceLen = 2;
        int insertedGunPos = backInPlacePos + backInPlaceLen;
        int insertedGunLen = 2;
        int outputVoltagePos = insertedGunPos + insertedGunLen;
        int outputVoltageLen = 4;
        int outputCurrentPos = outputVoltagePos + outputVoltageLen;
        int outputCurrentLen = 4;
        int gunLineTemPos = outputCurrentPos + outputCurrentLen;
        int gunLineTemLen = 2;
        int gunCodePos = gunLineTemPos + gunLineTemLen;
        int gunCodeLen = 16;
        int socPos = gunCodePos + gunCodeLen;
        int socLen = 2;
        int batterypackMaxTemPos = socPos + socLen;
        int batterypackMaxTemLen = 2;
        int accumulatedChargingTimePos = batterypackMaxTemPos + batterypackMaxTemLen;
        int accumulatedChargingTimeLen = 4;
        int remainingTimePos = accumulatedChargingTimePos + accumulatedChargingTimeLen;
        int remainingTimeLen = 4;
        int chargingDegreePos = remainingTimePos + remainingTimeLen;
        int chargingDegreeLen = 8;
        int calculatedChargingDegreePos = chargingDegreePos + chargingDegreeLen;
        int calculatedChargingDegreeLen = 8;
        int rechargeAmountPos = calculatedChargingDegreePos + calculatedChargingDegreeLen;
        int rechargeAmountLen = 8;
        int hardwareFailurePos = rechargeAmountPos + rechargeAmountLen;
        int hardwareFailureLen = 4;

        String machineCode = YunCarUtil.getTargetString(data, machineCodePos, machineCodeLen);
        String portCode = YunCarUtil.getTargetString(data, portCodePos, portCodeLen);

        ChargeProcessAnalysisPo chargeProcessAnalysisPo = new ChargeProcessAnalysisPo();
        chargeProcessAnalysisPo.setGunNum(portCode); // 枪编号
        //chargeProcessAnalysisPo.setOrderId(chargeMachineOrderDtos.get(0).getOrderId()); // 订单ID
        chargeProcessAnalysisPo.setAccumulatedChargingTime(YunCarUtil.getTargetBin(data,accumulatedChargingTimePos,accumulatedChargingTimeLen)); //累计充电时间单位：min；待机置零
        chargeProcessAnalysisPo.setOutputVoltage(YunCarUtil.getTargetBin(data,outputVoltagePos,outputVoltageLen,1)); // 输出电压
        chargeProcessAnalysisPo.setSoc(YunCarUtil.getTargetBin(data,socPos,socLen));// SOC
        //chargeProcessAnalysisPo.setCpaId(GenerateCodeFactory.getGeneratorId("11"));
        chargeProcessAnalysisPo.setRemark(""); // 备注
        chargeProcessAnalysisPo.setChargingDegree(YunCarUtil.getTargetBin(data,chargingDegreePos,chargingDegreeLen)); //  充电度数 精确到小数点后四位；待机置零
        chargeProcessAnalysisPo.setInsertedGun(YunCarUtil.getTargetBin(data,insertedGunPos,insertedGunLen)); // 是否插枪
        chargeProcessAnalysisPo.setOutputCurrent(YunCarUtil.getTargetBin(data,outputCurrentPos,outputCurrentLen));//输出电流
        chargeProcessAnalysisPo.setRemainingTime(YunCarUtil.getTargetBin(data,remainingTimePos,remainingTimeLen));//剩余时间单位：min；待机置零、交流桩置零
        chargeProcessAnalysisPo.setGunLineTem(YunCarUtil.getTargetBin(data,gunLineTemPos,gunLineTemLen)); //枪线温度
        chargeProcessAnalysisPo.setBackInPlace(YunCarUtil.getTargetBin(data,backInPlacePos,backInPlaceLen)); // 枪是否归位 0x00 否 0x01 是 0x02未知
        chargeProcessAnalysisPo.setBatterypackMaxTem(YunCarUtil.getTargetBin(data,batterypackMaxTemPos,batterypackMaxTemLen)); //电池组最高温度
        //chargeProcessAnalysisPo.setMachineId(chargeMachineDto.getMachineId());
        chargeProcessAnalysisPo.setPileNum(machineCode); // 桩编号
        chargeProcessAnalysisPo.setCalculatedChargingDegree(YunCarUtil.getTargetBin(data,calculatedChargingDegreePos,calculatedChargingDegreeLen)); //计损充电度数
        chargeProcessAnalysisPo.setHardwareFailure(YunCarUtil.getTargetBin(data,hardwareFailurePos,hardwareFailureLen));//硬件故障
        chargeProcessAnalysisPo.setRechargeAmount(YunCarUtil.getTargetBin(data,rechargeAmountPos,rechargeAmountLen));//已充金额
        chargeProcessAnalysisPo.setGunCode(YunCarUtil.getTargetBin(data,gunCodePos,gunCodeLen));//枪线编码
        //chargeProcessAnalysisPo.setCommunityId(chargeMachineDto.getCommunityId());

        System.out.println(JSONObject.toJSONString(chargeProcessAnalysisPo));
    }
}
