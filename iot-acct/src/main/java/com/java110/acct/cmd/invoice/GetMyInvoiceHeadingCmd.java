package com.java110.acct.cmd.invoice;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.CmdContextUtils;
import com.java110.core.utils.ListUtil;
import com.java110.dto.user.UserDto;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.acct.mall.ISendMall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

/**
 * 按登录手机号查询商城发票抬头
 * 服务编码：invoice.getMyInvoiceHeading
 */
@Java110Cmd(serviceCode = "invoice.getMyInvoiceHeading")
public class GetMyInvoiceHeadingCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(GetMyInvoiceHeadingCmd.class);

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    @Autowired
    private ISendMall sendMallImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        String userId = CmdContextUtils.getLoginUserId(context);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);
        if (ListUtil.isNull(userDtos)) {
            throw new CmdException("用户不存在");
        }
        String phone = userDtos.get(0).getTel();

        JSONObject paramIn = new JSONObject();
        paramIn.put("mallApiCode", "queryInvoiceHeadingBmoImpl");
        paramIn.put("phone", phone);

        ResultVo resultVo = sendMallImpl.post("/mall/api/common.openCommonApi", paramIn);
        if (resultVo.getCode() != ResultVo.CODE_OK) {
            throw new CmdException(resultVo.getMsg());
        }
        if (resultVo.getData() == null) {
            throw new CmdException("商城系统未部署");
        }

        JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(resultVo.getData()));
        if (!data.containsKey("heading")) {
            data.put("heading", null);
        }

        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                new ResultVo(ResultVo.CODE_OK, ResultVo.MSG_OK, data).toString(), HttpStatus.OK);
        context.setResponseEntity(responseEntity);
    }
}
