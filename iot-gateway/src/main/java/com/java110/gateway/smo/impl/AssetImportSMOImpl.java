package com.java110.gateway.smo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.ValidateResult;
import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.*;
import com.java110.dto.communityMember.CommunityMemberDto;
import com.java110.dto.log.AssetImportLogDetailDto;
import com.java110.dto.store.StoreDto;
import com.java110.dto.storeStaff.StoreStaffDto;
import com.java110.gateway.service.IGetCommunityStoreInfoSMO;
import com.java110.intf.job.IUserImportDataV1InnerServiceSMO;
import com.java110.intf.system.IAssetImportLogDetailInnerServiceSMO;
import com.java110.intf.system.IAssetImportLogInnerServiceSMO;
import com.java110.po.log.AssetImportLogDetailPo;
import com.java110.po.log.AssetImportLogPo;
import com.java110.gateway.importData.IImportDataCleaningAdapt;
import com.java110.gateway.smo.IAssetImportSMO;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class AssetImportSMOImpl implements IAssetImportSMO {

    private final static Logger logger = LoggerFactory.getLogger(AssetImportSMOImpl.class);

    /**
     * 导入最大行数
     */
    public static final int MAX_LINE = 2000;

    /**
     * 导入最大行数
     */
    public static final int DEFAULT_ROWS = 200;


    @Autowired
    private IAssetImportLogInnerServiceSMO assetImportLogInnerServiceSMOImpl;

    @Autowired
    private IAssetImportLogDetailInnerServiceSMO assetImportLogDetailInnerServiceSMOImpl;

    @Autowired
    private IUserImportDataV1InnerServiceSMO userImportDataV1InnerServiceSMOImpl;

    @Autowired
    private IGetCommunityStoreInfoSMO getCommunityStoreInfoSMOImpl;


    @Override
    public ResponseEntity<String> importData(String data, MultipartFile uploadFile, HttpServletRequest request) {

        JSONObject paramIn = JSONObject.parseObject(data);
        Assert.hasKeyAndValue(paramIn, "importAdapt", "未包含模板");
        String importAdapt = paramIn.getString("importAdapt") + "DataCleaning";



        IImportDataCleaningAdapt importDataCleaningAdapt = ApplicationContextFactory.getBean(importAdapt, IImportDataCleaningAdapt.class);
        if (importDataCleaningAdapt == null) {
            throw new IllegalArgumentException("适配器没有实现" + importAdapt);
        }
        try {

            Map<String, String> headers = new HashMap<String, String>();
            this.getRequestInfo(request, headers);
            headers.put(CommonConstant.HTTP_SERVICE, "assetImport");
            headers.put(CommonConstant.HTTP_METHOD, CommonConstant.HTTP_METHOD_POST);

            ValidateResult result = this.validateStoreStaffCommunityRelationship(data, headers);
            paramIn.put("storeId", result.getStoreId());

            Workbook workbook = null;  //工作簿

            workbook = ImportExcelUtils.createWorkbook(uploadFile);

            // todo 适配器封装数据
            List datas = importDataCleaningAdapt.analysisExcel(workbook, paramIn, null);

            if (datas == null || datas.size() > MAX_LINE) {
                throw new IllegalArgumentException("数据为空，或者数据行数大于" + MAX_LINE);
            }

            // 保存数据
            return saveLogAndImportData( datas, result, paramIn.getString("importAdapt"));
        } catch (Exception e) {
            logger.error("导入失败 ", e);
            return new ResponseEntity<String>("非常抱歉，您填写的模板数据有误：" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    protected ValidateResult validateStoreStaffCommunityRelationship(String body, Map<String, String> headers) {
        // 校验 员工和商户是否有关系
        String userId = headers.get("user-id");
        StoreStaffDto storeStaffDto = getCommunityStoreInfoSMOImpl.getStoreInfo(userId);

        if (storeStaffDto == null) {
            throw new IllegalArgumentException("商户不存在");
        }

        Assert.hasLength(storeStaffDto.getStoreId(), "根据用户ID查询商户ID失败，未包含storeId节点");
        Assert.hasLength(storeStaffDto.getStoreTypeCd(), "根据用户ID查询商户类型失败，未包含storeTypeCd节点");

        //开发者和运营不校验小区
        if (StoreDto.STORE_TYPE_ADMIN.equals(storeStaffDto.getStoreTypeCd()) || StoreDto.STORE_TYPE_DEV.equals(storeStaffDto.getStoreTypeCd())) {
            return new ValidateResult(storeStaffDto.getStoreId(), storeStaffDto.getStoreTypeCd(), "", userId, "-");
        }

        JSONObject paramIn = JSONObject.parseObject(body);

        String communityId = "";
        if (paramIn != null && paramIn.containsKey("communityId")
                && !StringUtil.isEmpty(paramIn.getString("communityId"))
                && !"-1".equals(paramIn.getString("communityId"))) {
            communityId = paramIn.getString("communityId");
            checkStoreEnterCommunity(storeStaffDto.getStoreId(), storeStaffDto.getStoreTypeCd(), communityId, userId);
        }
        return new ValidateResult(storeStaffDto.getStoreId(), storeStaffDto.getStoreTypeCd(), communityId, userId, "-");
    }


    protected void checkStoreEnterCommunity(String storeId, String storeTypeCd, String communityId, String userId) {
        Assert.hasLength(userId, "用户未登录请先登录");
        List<CommunityMemberDto> communityMemberDtos = getCommunityStoreInfoSMOImpl.getStoreEnterCommunitys(storeId, storeTypeCd);
        if (communityMemberDtos == null || communityMemberDtos.size() < 1) {
            throw new IllegalArgumentException("还未入驻小区，请先入驻小区");
        }

        CommunityMemberDto currentCommunity = getCurrentCommunity(communityMemberDtos, communityId);

        if (currentCommunity == null) {
            throw new IllegalArgumentException("传入小区ID非法，请正常操作");
        }

    }


    private CommunityMemberDto getCurrentCommunity(List<CommunityMemberDto> communityMemberDtos, String communityId) {
        for (CommunityMemberDto communityMemberDto : communityMemberDtos) {
            if (communityId.equals(communityMemberDto.getCommunityId())) {
                return communityMemberDto;
            }
        }

        return null;
    }

    private void getRequestInfo(HttpServletRequest request, Map headers) throws Exception {
        try {
            RequestUtils.initHeadParam(request, headers);
            RequestUtils.initUrlParam(request, headers);
            this.getUserInfo(request, headers);

        } catch (Exception e) {
            logger.error("加载头信息失败", e);
            throw e;
        }
    }


    private void getUserInfo(HttpServletRequest request, Map headers) throws Exception {
        Object claimsObj = request.getAttribute("claims");
        if (claimsObj == null) {
            return;
        }
        Map<String, String> claims = (Map<String, String>) claimsObj;

        for (String key : claims.keySet()) {

            if ("userId".equals(key)) {
                headers.put("user-id", claims.get(key));
            }
            headers.put(key, claims.get(key));
        }
    }



    /**
     * 处理ExcelData数据
     *
     * @param datas 数据
     */
    private ResponseEntity<String> saveLogAndImportData(
            List datas,
            ValidateResult result, String logType) {
        ResponseEntity<String> responseEntity = null;

        String logId = GenerateCodeFactory.getGeneratorId("10");

        AssetImportLogPo assetImportLogPo = new AssetImportLogPo();
        assetImportLogPo.setCommunityId(result.getCommunityId());
        assetImportLogPo.setLogId(logId);
        assetImportLogPo.setLogType(logType);
        assetImportLogPo.setErrorCount("0");
        assetImportLogPo.setSuccessCount("0");
        assetImportLogInnerServiceSMOImpl.saveAssetImportLog(assetImportLogPo);

        List<AssetImportLogDetailPo> assetImportLogDetailPos = new ArrayList<>();
        AssetImportLogDetailPo assetImportLogDetailPo = null;
        int flag = 0;
        Calendar createTimeCal = Calendar.getInstance();
        for (Object data : datas) {
            createTimeCal.add(Calendar.SECOND,1);
            assetImportLogDetailPo = new AssetImportLogDetailPo();
            assetImportLogDetailPo.setDetailId(GenerateCodeFactory.getGeneratorId("11"));
            assetImportLogDetailPo.setLogId(logId);
            assetImportLogDetailPo.setState(AssetImportLogDetailDto.STATE_WAIT_IMPORT);
            assetImportLogDetailPo.setMessage("待导入");
            assetImportLogDetailPo.setCommunityId(result.getCommunityId());
            assetImportLogDetailPo.setContent(JSONObject.toJSONString(data));
            assetImportLogDetailPo.setCreateTime(DateUtil.getFormatTimeStringA(createTimeCal.getTime()));
            assetImportLogDetailPos.add(assetImportLogDetailPo);
            if (assetImportLogDetailPos.size() > DEFAULT_ROWS) {
                flag = assetImportLogDetailInnerServiceSMOImpl.saveAssetImportLogDetails(assetImportLogDetailPos);
                if (flag < 1) {
                    throw new IllegalArgumentException("保存失败");
                }
                assetImportLogDetailPos = new ArrayList<>();
            }
        }

        if (assetImportLogDetailPos.size() > 0) {
            flag = assetImportLogDetailInnerServiceSMOImpl.saveAssetImportLogDetails(assetImportLogDetailPos);
            if (flag < 1) {
                throw new IllegalArgumentException("保存失败");
            }
        }

        // todo 调用 导入队列开始导入
        flag = userImportDataV1InnerServiceSMOImpl.importExcelData(assetImportLogPo);
        if (flag > 0) {
            return ResultVo.createResponseEntity(assetImportLogPo);
        }
        return ResultVo.error("导入失败");
    }

}
