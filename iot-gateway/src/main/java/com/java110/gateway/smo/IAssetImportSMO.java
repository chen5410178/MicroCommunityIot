package com.java110.gateway.smo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 数据导入
 */
public interface IAssetImportSMO {
    /**
     * 数据导入
     * @param data
     * @param uploadFile
     * @param request
     * @return
     */
    ResponseEntity<String> importData(String data, MultipartFile uploadFile, HttpServletRequest request);
}
