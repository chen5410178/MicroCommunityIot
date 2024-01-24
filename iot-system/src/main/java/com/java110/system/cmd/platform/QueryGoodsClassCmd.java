package com.java110.system.cmd.platform;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.RestTemplate;
import com.java110.core.cmd.Cmd;
import com.java110.core.cmd.CmdEvent;
import com.java110.core.constant.MappingConstant;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.exception.CmdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Java110Cmd(serviceCode = "product.queryGoodsClasses")
public class QueryGoodsClassCmd extends Cmd {

    private static Logger logger = LoggerFactory.getLogger(QueryGoodsClassCmd.class);

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException, ParseException {
        super.validatePageInfo(reqJson);
    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException, ParseException {
        String urlStr = MappingCache.getValue(MappingConstant.MICRO_INDUSTRY_PLATFORM_MALL,"url") + "/iot/api/goodsClass.listGoodsClasses?page=" + reqJson.getString("page") + "&row=" + reqJson.getString("row");
        URL url = null;
        ResponseEntity<String> responseEntity = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("APP-ID", "992023010100000001");
            conn.setRequestProperty("TRANSACTION-ID", UUID.randomUUID().toString());
            conn.setRequestProperty("REQ-TIME", new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
            conn.setRequestProperty("USER-ID", "-1");

            conn.connect();

            if (conn.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine())!=null){
                    stringBuffer.append(line);
                }
                bufferedReader.close();
                responseEntity = new ResponseEntity<String>(stringBuffer.toString(), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cmdDataFlowContext.setResponseEntity(responseEntity);
    }
}
