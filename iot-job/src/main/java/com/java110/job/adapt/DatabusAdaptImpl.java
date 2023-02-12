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
package com.java110.job.adapt;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.businessDatabus.CustomBusinessDatabusDto;
import com.java110.bean.dto.order.BusinessDto;
import com.java110.core.client.RestTemplate;
import com.java110.core.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * @desc add by 吴学文 15:21
 */
public abstract class DatabusAdaptImpl implements IDatabusAdapt {
    private static Logger logger = LoggerFactory.getLogger(DatabusAdaptImpl.class);


    /**
     * 开门
     *
     * @param paramIn 业务信息
     * @return
     */
    @Override
    public ResultVo closeDoor(JSONObject paramIn) {
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }

    @Override
    public ResultVo getQRcode(JSONObject reqJson) {
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }

    @Override
    public ResultVo customCarInOut(JSONObject reqJson) {
        return new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK);
    }


    /**
     * 业主处理执行
     *
     * @param business   当前处理业务
     * @param businesses 所有业务信息
     */
    @Override
    public void execute(BusinessDto business, List<BusinessDto> businesses) throws Exception {

    }

    /**
     * 手工 送数据
     *
     * @param customBusinessDatabusDto
     */
    public void customExchange(CustomBusinessDatabusDto customBusinessDatabusDto) {

    }

}
