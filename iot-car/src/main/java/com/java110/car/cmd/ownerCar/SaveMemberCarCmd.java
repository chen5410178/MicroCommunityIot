/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.car.cmd.ownerCar;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.cache.MappingCache;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.utils.*;
import com.java110.dto.parking.ParkingSpaceDto;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.intf.car.IOwnerCarV1InnerServiceSMO;
import com.java110.intf.car.IParkingSpaceV1InnerServiceSMO;
import com.java110.po.ownerCar.OwnerCarPo;
import com.java110.po.parking.ParkingSpacePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 类表述：保存
 * 服务编码：ownerCar.saveMemberCar
 * 请求路劲：/app/ownerCar.SaveOwnerCar
 * add by 吴学文 at 2023-08-23 15:16:36 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "ownerCar.saveMemberCar")
public class SaveMemberCarCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(SaveMemberCarCmd.class);

    @Autowired
    private IOwnerCarV1InnerServiceSMO ownerCarV1InnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        Assert.hasKeyAndValue(reqJson, "communityId", "未包含小区ID");
        Assert.hasKeyAndValue(reqJson, "carNum", "请求报文中未包含carNum");
        Assert.hasKeyAndValue(reqJson, "carType", "请求报文中未包含carType");
        Assert.hasKeyAndValue(reqJson, "carId", "请求报文中未包含carId");


        //todo 校验主车辆是否存在

        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setMemberId(reqJson.getString("carId"));
        ownerCarDto.setCarTypeCds(new String[]{OwnerCarDto.CAR_TYPE_PRIMARY});
        List<OwnerCarDto> masterCarDtos = ownerCarV1InnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        if (ListUtil.isNull(masterCarDtos)) {
            throw new CmdException("主车辆不存在");
        }


        //校验车牌号是否存在
        ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        ownerCarDto.setCarNum(reqJson.getString("carNum"));
        ownerCarDto.setPaIds(new String[]{masterCarDtos.get(0).getPaId()});
        ownerCarDto.setCarTypeCds(new String[]{OwnerCarDto.CAR_TYPE_PRIMARY, OwnerCarDto.CAR_TYPE_MEMBER}); // 临时车除外
        int count = ownerCarV1InnerServiceSMOImpl.queryOwnerCarsCount(ownerCarDto);

        if (count > 0) {
            throw new IllegalArgumentException("车辆已存在");
        }

        reqJson.put("masterOwnerCarDto",masterCarDtos.get(0));
    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

        OwnerCarDto masterOwnerCarDto = (OwnerCarDto)reqJson.get("masterOwnerCarDto");
        OwnerCarPo ownerCarPo = BeanConvertUtil.covertBean(masterOwnerCarDto, OwnerCarPo.class);
        ownerCarPo.setMemberId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_carId));
        ownerCarPo.setCarNum(reqJson.getString("carNum"));
        ownerCarPo.setCarType(reqJson.getString("carType"));
        ownerCarPo.setRemark(reqJson.getString("remark"));
        ownerCarPo.setCarTypeCd(OwnerCarDto.CAR_TYPE_MEMBER);

        int flag = ownerCarV1InnerServiceSMOImpl.saveOwnerCar(ownerCarPo);
        if (flag < 1) {
            throw new CmdException("保存车辆属性失败");
        }

    }


}
