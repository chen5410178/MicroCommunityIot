package com.java110.user.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.file.FileRelDto;
import com.java110.bean.dto.owner.OwnerDto;
import com.java110.bean.dto.room.RoomDto;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.BeanConvertUtil;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.*;
import com.java110.intf.community.IRoomInnerServiceSMO;
import com.java110.intf.system.IFileRelInnerServiceSMO;
import com.java110.intf.user.IOwnerInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@Java110CmdDoc(title = "查询业主",
        description = "第三方系统，比如招商系统查询业主信息",
        httpMethod = "get",
        url = "http://{ip}:{port}/app/owner.queryOwners",
        resource = "userDoc",
        author = "吴学文",
        serviceCode = "owner.queryOwners",
        seq = 12
)

@Java110ParamsDoc(params = {
        @Java110ParamDoc(name = "page", type = "int", length = 11, remark = "页数"),
        @Java110ParamDoc(name = "row", type = "int", length = 11, remark = "行数"),
        @Java110ParamDoc(name = "communityId", length = 30, remark = "小区ID"),
        @Java110ParamDoc(name = "name", length = 64, remark = "业主名称"),
        @Java110ParamDoc(name = "link", length = 11, remark = "业主手机号"),
        @Java110ParamDoc(name = "idCard", length = 30, remark = "业主身份证号"),
        @Java110ParamDoc(name = "ownerTypeCd", length = 12, remark = "业主类型 1001 业主 2002 家庭成员 家庭成员 需要传业主的ownerId"),
        @Java110ParamDoc(name = "ownerId", length = 30, remark = "业主ID，家庭成员业主ID"),
        @Java110ParamDoc(name = "memberId", length = 30, remark = "业主ID，主键ID"),
})

@Java110ResponseDoc(
        params = {
                @Java110ParamDoc(name = "code", type = "int", length = 11, defaultValue = "0", remark = "返回编号，0 成功 其他失败"),
                @Java110ParamDoc(name = "msg", type = "String", length = 250, defaultValue = "成功", remark = "描述"),
                @Java110ParamDoc(name = "owners", type = "Array", length = -1, defaultValue = "成功", remark = "数据"),
                @Java110ParamDoc(parentNodeName = "owners", name = "communityId", length = 30, remark = "小区ID"),
                @Java110ParamDoc(parentNodeName = "owners", name = "name", length = 64, remark = "业主名称"),
                @Java110ParamDoc(parentNodeName = "owners", name = "link", length = 11, remark = "业主手机号"),
                @Java110ParamDoc(parentNodeName = "owners", name = "idCard", length = 30, remark = "业主身份证号"),
                @Java110ParamDoc(parentNodeName = "owners", name = "address", length = 512, remark = "地址"),
                @Java110ParamDoc(parentNodeName = "owners", name = "sex", length = 12, remark = "性别 男 1 女 0"),
                @Java110ParamDoc(parentNodeName = "owners", name = "ownerTypeCd", length = 12, remark = "业主类型 1001 业主 2002 家庭成员 家庭成员 需要传业主的ownerId"),
                @Java110ParamDoc(parentNodeName = "owners", name = "remark", length = 512, remark = "备注"),
                @Java110ParamDoc(parentNodeName = "owners", name = "memberId", length = 30, remark = "业主ID"),
                @Java110ParamDoc(parentNodeName = "owners", name = "ownerPhoto", length = -1, remark = "业主人脸 用于同步门禁 人脸开门"),
        }
)

@Java110ExampleDoc(
        reqBody = "http://ip:port/app/owner.queryOwners?ownerTypeCd=1001&page=1&row=10&communityId=2022121921870161",
        resBody = "{\n" +
                "\t\"owners\": [{\n" +
                "\t\t\"address\": \"张三\",\n" +
                "\t\t\"idCard\": \"\",\n" +
                "\t\t\"link\": \"18909718888\",\n" +
                "\t\t\"memberId\": \"772023012589770046\",\n" +
                "\t\t\"name\": \"王王\",\n" +
                "\t\t\"ownerId\": \"772023012589770046\",\n" +
                "\t\t\"ownerTypeCd\": \"1001\",\n" +
                "\t\t\"remark\": \"\",\n" +
                "\t\t\"sex\": \"0\",\n" +
                "\t\t\"userName\": \"人和物业\"\n" +
                "\t}],\n" +
                "\t\"page\": 0,\n" +
                "\t\"records\": 1,\n" +
                "\t\"rows\": 0,\n" +
                "\t\"total\": 1\n" +
                "}"
)
@Java110Cmd(serviceCode = "owner.queryOwners")
public class QueryOwnersCmd extends Cmd {

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;


    @Autowired
    private IRoomInnerServiceSMO roomInnerServiceSMOImpl;

    @Autowired
    private IFileRelInnerServiceSMO fileRelInnerServiceSMOImpl;


    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        Assert.jsonObjectHaveKey(reqJson, "page", "请求中未包含page信息");
        Assert.jsonObjectHaveKey(reqJson, "row", "请求中未包含row信息");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求中未包含communityId信息");
        Assert.jsonObjectHaveKey(reqJson, "ownerTypeCd", "请求中未包含ownerTypeCd信息");
        Assert.isInteger(reqJson.getString("page"), "不是有效数字");
        Assert.isInteger(reqJson.getString("row"), "不是有效数字");
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {
        //根据房屋查询时 先用 房屋信息查询 业主ID
        freshRoomId(reqJson);
        //查询总记录数
        int total = ownerInnerServiceSMOImpl.queryOwnersCount(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
        List<OwnerDto> ownerDtos = null;
        if (total > 0) {
            ownerDtos = ownerInnerServiceSMOImpl.queryOwners(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));

            for (OwnerDto ownerDto : ownerDtos) {
                //查询照片
                FileRelDto fileRelDto = new FileRelDto();
                fileRelDto.setObjId(ownerDto.getMemberId());
                fileRelDto.setRelTypeCd("10000"); //业主照片
                List<FileRelDto> fileRelDtos = fileRelInnerServiceSMOImpl.queryFileRels(fileRelDto);
                if (fileRelDtos != null && fileRelDtos.size() > 0) {
                    List<String> urls = new ArrayList<>();
                    for (FileRelDto fileRel : fileRelDtos) {
                        urls.add(fileRel.getFileRealName());
                    }
                    ownerDto.setUrls(urls);
                }
            }
        }
        ResultVo resultVo = new ResultVo((int) Math.ceil((double) total / (double) reqJson.getInteger("row")), total, ownerDtos);

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(resultVo.toString(), HttpStatus.OK);

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }

    private void freshRoomId(JSONObject reqJson) {
        if (!reqJson.containsKey("roomName")) {
            return;
        }
        String roomName = reqJson.getString("roomName");
        if (StringUtil.isEmpty(roomName)) {
            return;
        }
        if (!roomName.contains("-")) {
            throw new IllegalArgumentException("房屋格式错误,请写入如 楼栋-单元-房屋 格式");
        }
        String[] params = roomName.split("-", 3);
        if (params.length != 3) {
            throw new IllegalArgumentException("房屋格式错误,请写入如 楼栋-单元-房屋 格式");
        }
        RoomDto roomDto = new RoomDto();
        roomDto.setFloorNum(params[0]);
        roomDto.setUnitNum(params[1]);
        roomDto.setRoomNum(params[2]);
        roomDto.setCommunityId(reqJson.getString("communityId"));
        List<RoomDto> roomDtos = roomInnerServiceSMOImpl.queryRooms(roomDto);
        Assert.listOnlyOne(roomDtos, "未查询到房屋下业主信息");
        reqJson.put("roomId", roomDtos.get(0).getRoomId());
    }

}
