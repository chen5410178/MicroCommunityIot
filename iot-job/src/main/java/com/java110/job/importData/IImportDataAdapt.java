package com.java110.job.importData;

import com.java110.dto.log.AssetImportLogDetailDto;

import java.util.List;

/**
 * 导入数据适配器
 */
public interface IImportDataAdapt {


    /**
     * 导入数据
     * @param assetImportLogDetailDtos
     */
    void importData(List<AssetImportLogDetailDto> assetImportLogDetailDtos);
}
