package com.java110.job.export.adapt;

import com.alibaba.fastjson.JSONObject;
import com.java110.bean.dto.car.OwnerCarDto;
import com.java110.bean.dto.data.ExportDataDto;
import com.java110.core.utils.DateUtil;
import com.java110.intf.car.IOwnerCarInnerServiceSMO;
import com.java110.job.export.IExportDataAdapt;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("exportOwnerCar")
public class ExportOwnerCarAdapt implements IExportDataAdapt {
    @Autowired
    private IOwnerCarInnerServiceSMO ownerCarInnerServiceSMOImpl;

    @Override
    public SXSSFWorkbook exportData(ExportDataDto exportDataDto) {
        SXSSFWorkbook workbook = null;  //工作簿
        //工作表
        workbook = new SXSSFWorkbook();
        workbook.setCompressTempFiles(false);

        Sheet sheet = workbook.createSheet("业主车辆表");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("车牌号");
        row.createCell(1).setCellValue("业主");
        row.createCell(2).setCellValue("手机号");
        row.createCell(3).setCellValue("车辆类型");
        row.createCell(4).setCellValue("车牌类型");
        row.createCell(5).setCellValue("起租时间");
        row.createCell(6).setCellValue("截止时间");

        JSONObject reqJson = exportDataDto.getReqJson();

        //查询数据
        doExportOwnerCar(sheet, reqJson);

        return workbook;
    }

    private void doExportOwnerCar(Sheet sheet, JSONObject reqJson) {
        OwnerCarDto ownerCarDto = new OwnerCarDto();
        ownerCarDto.setCommunityId(reqJson.getString("communityId"));
        List<OwnerCarDto> OwnerCarDtos = ownerCarInnerServiceSMOImpl.queryOwnerCars(ownerCarDto);

        for (int index = 0; index < OwnerCarDtos.size(); index++) {
            appendData(sheet, index + 1, OwnerCarDtos.get(index));
        }
    }

    private void appendData(Sheet sheet, int i, OwnerCarDto ownerCarDto) {
        Row row = null;
        row = sheet.createRow(i);
        row.createCell(0).setCellValue(ownerCarDto.getCarNum());
        row.createCell(1).setCellValue(ownerCarDto.getOwnerName());
        row.createCell(2).setCellValue(ownerCarDto.getLink());
        row.createCell(3).setCellValue(ownerCarDto.getCarTypeName());
        if (OwnerCarDto.LEASE_TYPE_TEMP.equals(ownerCarDto.getLeaseType())) {
            row.createCell(4).setCellValue("临时车");
        } else {
            row.createCell(4).setCellValue(ownerCarDto.getLeaseTypeName());
        }
        row.createCell(5).setCellValue(DateUtil.getFormatTimeStringB(ownerCarDto.getStartTime()));
        row.createCell(6).setCellValue(DateUtil.getFormatTimeStringB(ownerCarDto.getEndTime()));

    }
}
