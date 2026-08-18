package com.java110.gateway.importData.adapt;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.ValidateResult;
import com.java110.core.utils.Assert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.ImportExcelUtils;
import com.java110.core.utils.StringUtil;
import com.java110.dto.importData.ImportShopDto;
import com.java110.gateway.importData.DefaultImportDataAdapt;
import com.java110.gateway.importData.IImportDataCleaningAdapt;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("importShopDataCleaning")
public class ImportShopDataCleaningAdapt extends DefaultImportDataAdapt implements IImportDataCleaningAdapt {

    @Override
    public List analysisExcel(Workbook workbook, JSONObject paramIn, ValidateResult result) throws Exception {
        List<ImportShopDto> importShops = new ArrayList<>();
        getShops(workbook, importShops, result);
        validateShopInfo(importShops);
        return importShops;
    }

    private void getShops(Workbook workbook, List<ImportShopDto> importShops, ValidateResult result) {
        Sheet sheet = ImportExcelUtils.getSheet(workbook, "资产信息");
        List<Object[]> oList = ImportExcelUtils.listFromSheet(sheet);
        Set<String> shopKeys = new HashSet<>();
        for (int osIndex = 0; osIndex < oList.size(); osIndex++) {
            Object[] os = oList.get(osIndex);
            if (osIndex == 0 || osIndex == 1) { // 第一行是 头部信息 直接跳过
                continue;
            }
            if (StringUtil.isNullOrNone(os[0])) {
                continue;
            }
            Assert.hasValue(os[0], (osIndex + 1) + "商铺编号不能为空");
            Assert.hasValue(os[1], (osIndex + 1) + "商铺位置不能为空");
            Assert.hasValue(os[2], (osIndex + 1) + "建筑面积不能为空");
            Assert.hasValue(os[6], (osIndex + 1) + "状态不能为空");

            String shopNum = os[0].toString();
            if (hasSpecialCharacters(shopNum)) {
                throw new IllegalArgumentException((osIndex + 1) + "行商铺编号不能包含特殊符号（- # ? &）");
            }
            String key = shopNum + "-" + os[1].toString();
            if (!shopKeys.add(key)) {
                throw new IllegalArgumentException((osIndex + 1) + "行商铺编号[" + shopNum + "]在位置[" + os[1] + "]下重复");
            }

            String startTime = excelDoubleToDate(os[4] == null ? "" : os[4].toString());
            String endTime = excelDoubleToDate(os[5] == null ? "" : os[5].toString());
            if (!StringUtil.isEmpty(startTime)) {
                Assert.isDate(startTime, DateUtil.DATE_FORMATE_STRING_B, (osIndex + 1) + "行开始时间格式错误 请填写YYYY-MM-DD文本格式");
            }
            if (!StringUtil.isEmpty(endTime)) {
                Assert.isDate(endTime, DateUtil.DATE_FORMATE_STRING_B, (osIndex + 1) + "行结束时间格式错误 请填写YYYY-MM-DD文本格式");
            }

            ImportShopDto importShopDto = new ImportShopDto();
            importShopDto.setShopNum(shopNum);
            importShopDto.setLocationName(os[1].toString());
            importShopDto.setBuiltUpArea(os[2].toString());
            importShopDto.setShopArea(os[3] == null ? null : os[3].toString());
            importShopDto.setStartTime(startTime);
            importShopDto.setEndTime(endTime);
            importShopDto.setShopState(os[6].toString());
            importShopDto.setShopRent(os[7] == null ? null : os[7].toString());
            importShopDto.setOwnerName(os[8] == null ? null : os[8].toString());
            importShopDto.setSex(os[9] == null ? null : os[9].toString());
            importShopDto.setLink(os[10] == null ? null : os[10].toString());
            importShopDto.setIdCard(os[11] == null ? null : os[11].toString());
            importShopDto.setOwnerTypeCd(os[12] == null ? null : os[12].toString());
            importShopDto.setPersonType(os[13] == null ? null : os[13].toString());
            importShopDto.setPersonRole(os[14] == null ? null : os[14].toString());
            importShopDto.setContactPerson(os[15] == null ? null : os[15].toString());
            importShopDto.setReserveLink(os[16] == null ? null : os[16].toString());
            importShopDto.setCommunityId(result.getCommunityId());
            importShopDto.setUserId(result.getUserId());
            importShops.add(importShopDto);
        }
    }

    private void validateShopInfo(List<ImportShopDto> importShops) {
        for (ImportShopDto importShopDto : importShops) {
            if (!"2008".equals(importShopDto.getShopState())
                    && !"2006".equals(importShopDto.getShopState())
                    && !"2007".equals(importShopDto.getShopState())) {
                throw new IllegalArgumentException(importShopDto.getShopNum() + "状态应填写 2008(空置) 2006(已出租) 或者 2007 (已出售)");
            }
            if (!StringUtil.isEmpty(importShopDto.getSex())
                    && !"0".equals(importShopDto.getSex())
                    && !"1".equals(importShopDto.getSex())) {
                throw new IllegalArgumentException(importShopDto.getShopNum() + "性别应填写 0(男) 或者 1 (女)");
            }
        }
    }
}
