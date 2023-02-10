package com.java110.core.factory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * 数据流工厂类
 * Created by wuxw on 2018/4/13.
 */
public class DataFlowFactory {

    /**
     * 初始化
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class 不能为空");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "指定类是一个接口");
        }
        //DateUtil.getCurrentDate(), ResponseConstant.RESULT_CODE_SUCCESS
        try {
            return clazz.getConstructor(Date.class,String.class).newInstance(DateUtil.getCurrentDate(), ResultVo.CODE_OK);
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "是一个抽象类?", ex);
        }catch (Exception ex){
            throw new BeanInstantiationException(clazz, "构造函数不能访问?", ex);

        }
    }




    /**
     * hashmap 转MultiValueMap
     * @param httpHeaders
     * @return
     */
    public static MultiValueMap<String, String> hashMap2MultiValueMap(Map<String,String> httpHeaders){
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        for(String key:httpHeaders.keySet()) {
            multiValueMap.add(key,httpHeaders.get(key));
        }

        return multiValueMap;
    }


}
