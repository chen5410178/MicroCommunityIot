package com.java110.gateway.smo.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.ResultVo;
import com.java110.bean.dto.ValidateResult;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ImportExcelUtils;
import com.java110.dto.log.AssetImportLogDetailDto;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
            ValidateResult result = null;//this.validateStoreStaffCommunityRelationship(pd, restTemplate);
            paramIn.put("storeId", result.getStoreId());

            Workbook workbook = null;  //工作簿
            //工作表
            String[] headers = null;   //表头信息
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
