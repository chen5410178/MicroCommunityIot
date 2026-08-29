package com.java110.system.smo.impl;


import com.java110.bean.dto.file.FileDto;
import com.java110.core.cache.MappingCache;
import com.java110.core.client.FtpUploadTemplate;
import com.java110.core.client.JSchFtpUploadTemplate;
import com.java110.core.client.S3UploadTemplate;
import com.java110.core.constant.MappingConstant;
import com.java110.core.utils.S3Util;
import com.java110.intf.system.IFileInnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FileInnerServiceSMOImpl implements IFileInnerServiceSMO {

    private static final String ROOT_PATH = "hc/iot/";


    @Autowired
    private FtpUploadTemplate ftpUploadTemplate;

    @Autowired
    private JSchFtpUploadTemplate jSchFtpUploadTemplate;

    @Autowired
    private S3UploadTemplate s3UploadTemplate;


    @Override
    public String saveFile(@RequestBody FileDto fileDto) {

        //int saveFileFlag = fileServiceDaoImpl.saveFile(BeanConvertUtil.beanCovertMap(fileDto));
        String fileName = "";

        fileName = s3UploadTemplate.upload(fileDto.getContext(), ROOT_PATH);

        return fileName;
    }

    @Override
    public List<FileDto> queryFiles(@RequestBody FileDto fileDto) {
        //return BeanConvertUtil.covertBeanList(fileServiceDaoImpl.getFiles(BeanConvertUtil.beanCovertMap(fileDto)), FileDto.class);
        List<FileDto> fileDtos = new ArrayList<>();
        String fileName = fileDto.getFileSaveName();
        String ftpPath = "hc/";
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileName.contains("/")) {
            ftpPath += fileName.substring(0, fileName.lastIndexOf("/") + 1);
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        }
        String context = "";
        context = s3UploadTemplate.download(ftpPath, fileName, "",
                0, "",
                "");

        fileDto.setContext(context);
        fileDto.setSuffix(suffix);
        fileDtos.add(fileDto);
        return fileDtos;
    }


}
