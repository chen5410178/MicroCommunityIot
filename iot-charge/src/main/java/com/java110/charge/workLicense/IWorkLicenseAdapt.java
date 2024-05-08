package com.java110.charge.workLicense;

import com.java110.bean.ResultVo;
import com.java110.dto.workLicenseMachine.WorkLicenseMachineDto;
import com.java110.po.whiteList.WhiteListPo;

/**
 * 工作牌适配器
 */
public interface IWorkLicenseAdapt {

    /**
     * 设置参数
     *
     * @param workLicenseMachineDto
     * @return
     */
    ResultVo settingParam(WorkLicenseMachineDto workLicenseMachineDto);

    /**
     * 播放语音消息
     *
     * @param workLicenseMachineDto
     * @param ttsText
     * @return
     */
    ResultVo playTts(WorkLicenseMachineDto workLicenseMachineDto, String ttsText);
    /**
     * 设置通话白名单
     *
     * @param workLicenseMachineDto
     * @param whiteListPo
     * @return
     */
    ResultVo saveWhiteList(WorkLicenseMachineDto workLicenseMachineDto, WhiteListPo whiteListPo);

    /**
     * 接受消息
     *
     * @param workLicenseMachineDto
     * @param data
     * @return
     */
    ResultVo result(WorkLicenseMachineDto workLicenseMachineDto, byte[] data);

    boolean hasOnLine(WorkLicenseMachineDto workLicenseMachineDto);
}
