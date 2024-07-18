package com.java110.monitor.factory.oxtop;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.core.client.RestTemplate;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.utils.DateUtil;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.dto.monitorManufactor.MonitorManufactorDto;
import com.java110.monitor.factory.IMonitorModelAdapt;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

/**
 * INSERT INTO `hc_iot`.`t_dict` ( `status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`)
 * VALUES ( 'oxtopAdaptImpl', 'oxtop监控平台', 'oxtop监控平台', '2019-10-15 09:47:21', 'monitor_manufactor', 'mm_bean');
 * oxtop 监控平台
 */
@Service("oxtopAdaptImpl")
public class OxtopAdaptImpl implements IMonitorModelAdapt {
    private static Logger logger = LoggerFactory.getLogger(OxtopAdaptImpl.class);

    @Autowired
    private RestTemplate outRestTemplate;

    public static final String QUERY_VIDEO_URL = "/queryRealplayUri";

    @Override
    public void queryMonitorMachineState(MonitorMachineDto monitorMachineDto) {

    }

    /**
     * @return java.lang.String
     * @Author niuqiang
     * @Description //TODO 根据 ip 账号 密码 获取token
     * @Date 16:43 2024/7/9
     * @Param [ip, username, password]
     **/
    public String getAuthToken(String ip, String username, String password
    ) {
        if (ip == null || username == null || password == null) {
            throw new IllegalArgumentException("IP, username, and password must not be null");
        }
        String token = null;
        String loginUrl = ip + "/login";
        // 将密码进⾏MD5加密
        String md5Password = AuthenticationFactory.md5(password);
        // 创建Basic认证头
        String auth = username + ":" + md5Password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authHeader);
        headers.add("Content-Type", "application/json");
        HttpEntity httpEntity = new HttpEntity("", headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = outRestTemplate.exchange(loginUrl, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
            responseEntity = ResultVo.error(e.getMessage());
            logger.error("调用接口" + QUERY_VIDEO_URL + "异常：", e);
            throw e;
        } catch (Exception e) {
            logger.error("调用接口" + QUERY_VIDEO_URL + "异常：", e);
            throw e;
        } finally {
            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", loginUrl, httpEntity, responseEntity);
        }
        HttpHeaders resHeader = responseEntity.getHeaders();
        token = resHeader.getFirst("Token");
        return token;
    }

//    @Override
//    public String getVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto) {
//        String url = monitorManufactorDto.getMmUrl();
//        url = url.endsWith("/") ? (url + QUERY_VIDEO_URL) : (url + "/" + QUERY_VIDEO_URL);
//        String appId = monitorManufactorDto.getAppId();
//        String appSecure = monitorManufactorDto.getAppSecure();
//        String token = getAuthToken(monitorManufactorDto.getMmUrl(),appId,appSecure);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Token", token);
//        //headers.add("Content-Type", "application/octet-stream");
//        headers.add("Content-Type", "application/json");
//        JSONObject paramIn = new JSONObject();
//        paramIn.put("deviceId",monitorMachineDto.getMachineCode());
//        paramIn.put("channelNum",1);
//        paramIn.put("streamType",0);
//        paramIn.put("mediaType","FLV");
//        paramIn.put("tokenType","MyEyeAccount");
//        paramIn.put("ignoreState",true);
//        paramIn.put("expireTime","0");
//        paramIn.put("https",false);
//        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), headers);
//
//
//        ResponseEntity<String> responseEntity = null;
//        try {
//            responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
//        } catch (HttpStatusCodeException e) { //这里spring 框架 在4XX 或 5XX 时抛出 HttpServerErrorException 异常，需要重新封装一下
//            responseEntity = ResultVo.error(e.getMessage());
//            logger.error("调用接口" + QUERY_VIDEO_URL + "异常：", e);
//            throw e;
//        } catch (Exception e) {
//            logger.error("调用接口" + QUERY_VIDEO_URL + "异常：", e);
//            throw e;
//        } finally {
//            logger.debug("请求地址为,{} 请求中心服务信息，{},中心服务返回信息，{}", url, httpEntity, responseEntity);
//        }
//        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
//        if (paramOut.getIntValue("result") != 200) {
//            throw new IllegalArgumentException(paramOut.getString("msg"));
//        }
//
//        return paramOut.getString("uri");
//    }

    public String getVideoUrl(MonitorMachineDto monitorMachineDto, MonitorManufactorDto monitorManufactorDto)  {
        String url = monitorManufactorDto.getMmUrl();
        url = url.endsWith("/") ? (url + QUERY_VIDEO_URL) : (url + "/" + QUERY_VIDEO_URL);
        String appId = monitorManufactorDto.getAppId();
        String appSecure = monitorManufactorDto.getAppSecure();
        String token = getAuthToken(monitorManufactorDto.getMmUrl(), appId, appSecure);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Token", token);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept-Encoding", "identity"); // 使用 identity，表示不接受任何编码方式

        JSONObject paramIn = new JSONObject();
        paramIn.put("deviceId", monitorMachineDto.getMachineCode());
        paramIn.put("channelNum", 1);
        paramIn.put("streamType", 0);
        paramIn.put("mediaType", "FLV");
        paramIn.put("tokenType", "MyEyeAccount");
        paramIn.put("ignoreState", true);
        paramIn.put("expireTime", "0");
        paramIn.put("https", false);

        StringEntity entity = new StringEntity(paramIn.toJSONString(), "UTF-8");
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new IOException("Unexpected response status: " + statusCode);
            }

            org.apache.http.HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                throw new IOException("Response entity is null");
            }

          //  InputStream responseStream = responseEntity.getContent();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
//            try {
//                // 检查 Content-Encoding 头部
//                if (response.getFirstHeader("Content-Encoding") != null &&
//                        "gzip".equalsIgnoreCase(response.getFirstHeader("Content-Encoding").getValue())) {
//                    responseString = decompressGzip(responseStream);
//                } else {
//                    responseString = EntityUtils.toString(responseEntity, "UTF-8");
//                }
//            } catch (ZipException e) {
//                // 如果解压缩失败，尝试直接读取响应内容
//                logger.warn("Failed to decompress GZIP response, trying to read as plain text", e);
//                responseString = readStream(responseStream);
//            }

            JSONObject paramOut = JSONObject.parseObject(responseString);
            if (paramOut.getIntValue("result") != 200) {
                throw new IllegalArgumentException("Error from server: " + paramOut.getString("msg"));
            }

            String uri = paramOut.getString("uri");
            String mmUrl = monitorManufactorDto.getMmUrl();
            uri = mmUrl.substring(0,mmUrl.indexOf(":9000"))+uri.substring(uri.indexOf(":9050"));

            return uri;

        } catch (IOException e) {
            logger.error("Exception calling " + QUERY_VIDEO_URL + ": ", e);
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    private String decompressGzip(InputStream gzipStream) throws IOException {
        //try (GZIPInputStream gis = new GZIPInputStream(gzipStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toString("UTF-8");
        //}
    }

//    private String decompressGzip(InputStream gzipStream) throws IOException {
//        try (GZIPInputStream gis = new GZIPInputStream(gzipStream);
//             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = gis.read(buffer)) != -1) {
//                out.write(buffer, 0, len);
//            }
//            return out.toString("UTF-8");
//        }
//    }
}
