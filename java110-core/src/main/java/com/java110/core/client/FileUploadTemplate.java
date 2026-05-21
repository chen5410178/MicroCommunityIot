package com.java110.core.client;

import com.java110.core.cache.MappingCache;
import com.java110.core.constant.MappingConstant;
import com.java110.core.utils.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FileUploadTemplate {

    private static final String ROOT_PATH = "hc/";


    @Autowired
    private FtpUploadTemplate ftpUploadTemplate;

    @Autowired
    private JSchFtpUploadTemplate jSchFtpUploadTemplate;

    @Autowired
    private S3UploadTemplate s3UploadTemplate;



    public String saveFile(InputStream inputStream,String fileName){

        String newfileName = ROOT_PATH+ fileName;

        String ossSwitch = MappingCache.getValue(MappingConstant.FILE_DOMAIN, S3Util.S3_SWITCH);

        if (S3Util.S3_SWITCH_S3.equals(ossSwitch)) {
            newfileName = s3UploadTemplate.upload(inputStream, newfileName);
        } else {
            String ftpServer = MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_SERVER);
            int ftpPort = Integer.parseInt(MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_PORT));
            String ftpUserName = MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_USERNAME);
            String ftpUserPassword = MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_USERPASSWORD);

            newfileName = ftpUploadTemplate.upload(inputStream, ftpServer,
                    ftpPort, ftpUserName,
                    ftpUserPassword, newfileName);
        }
        return newfileName;
    }

    public InputStream downloadFile(String fileName){

        InputStream inputStream = null;

        String ossSwitch = MappingCache.getValue(MappingConstant.FILE_DOMAIN, S3Util.S3_SWITCH);

        if (S3Util.S3_SWITCH_S3.equals(ossSwitch)) {
            inputStream = s3UploadTemplate.download(fileName);
        } else {
            String ftpServer = MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_SERVER);
            int ftpPort = Integer.parseInt(MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_PORT));
            String ftpUserName = MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_USERNAME);
            String ftpUserPassword = MappingCache.getValue(FtpUploadTemplate.FTP_DOMAIN, FtpUploadTemplate.FTP_USERPASSWORD);

            inputStream = ftpUploadTemplate.download(fileName, ftpServer,
                    ftpPort, ftpUserName,
                    ftpUserPassword);
        }

        return inputStream;
    }
}
