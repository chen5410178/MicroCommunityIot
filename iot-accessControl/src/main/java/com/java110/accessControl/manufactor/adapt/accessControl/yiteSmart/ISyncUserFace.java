package com.java110.accessControl.manufactor.adapt.accessControl.yiteSmart;

import com.alibaba.fastjson.JSONObject;
import com.java110.dto.accessControl.AccessControlDto;
import com.java110.po.accessControlFace.AccessControlFacePo;

public interface ISyncUserFace {


    JSONObject getUserFace(AccessControlDto accessControlDto, AccessControlFacePo accessControlFacePo);
}
