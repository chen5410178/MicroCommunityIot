package com.java110.hal.rtsp;


import com.java110.core.exception.CmdException;
import com.java110.core.utils.Assert;
import com.java110.core.utils.ListUtil;
import com.java110.dto.monitor.MonitorMachineAttrsDto;
import com.java110.dto.monitor.MonitorMachineDto;
import com.java110.intf.monitor.IMonitorMachineAttrsV1InnerServiceSMO;
import com.java110.intf.monitor.IMonitorMachineV1InnerServiceSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

@Service
public class PlayVideoImpl implements IPlayVideo {

    @Autowired
    private IMonitorMachineV1InnerServiceSMO monitorMachineV1InnerServiceSMOImpl;

    @Autowired
    private IMonitorMachineAttrsV1InnerServiceSMO monitorMachineAttrsV1InnerServiceSMOImpl;


    @Override
    public String playVideo(String machineId) {

        MonitorMachineDto monitorMachineDto = new MonitorMachineDto();
        monitorMachineDto.setMachineId(machineId);
        List<MonitorMachineDto> machineDtos = monitorMachineV1InnerServiceSMOImpl.queryMonitorMachines(monitorMachineDto);

        Assert.listOnlyOne(machineDtos, "未包含监控设备");
//        if (MonitorMachineDto.PROTOCOL_RTSP.equals(machineDtos.get(0).getProtocol())) {
//            return playRtspVideo(machineDtos.get(0));
//        }
        return null;
    }

    /**
     * rtsp 流播放
     *
     * @param monitorMachineDto
     * @return
     */
    private String playRtspVideo(MonitorMachineDto monitorMachineDto) {


        MonitorMachineAttrsDto monitorMachineAttrsDto = new MonitorMachineAttrsDto();
        monitorMachineAttrsDto.setMachineId(monitorMachineDto.getMachineId());
        List<MonitorMachineAttrsDto> monitorMachineAttrsDtos = monitorMachineAttrsV1InnerServiceSMOImpl.queryMonitorMachineAttrss(monitorMachineAttrsDto);

        if (ListUtil.isNull(monitorMachineAttrsDtos)) {
            throw new CmdException("配置错误");
        }

        /**
         * 2001	machine_attr	地址
         * 2002	machine_attr	密码
         * 2003	machine_attr	账号
         */

        String url = MonitorMachineAttrsDto.getSpecValue(monitorMachineAttrsDtos,"2001");
        String password = MonitorMachineAttrsDto.getSpecValue(monitorMachineAttrsDtos,"2002");
        String name = MonitorMachineAttrsDto.getSpecValue(monitorMachineAttrsDtos,"2003");
        String videoPort = MonitorMachineAttrsDto.getSpecValue(monitorMachineAttrsDtos,"2004");
        int port = 554;
        String ip = url;
        if(url.contains(":")){
            ip = url.split(":")[0];
            port = Integer.parseInt(url.split(":")[1]);
        }
        RTSPClient rtspClient = new RTSPClient(url,"rtsp://"+url,name,password,monitorMachineDto.getMachineId());

        rtspClient.startup();

        return "";

    }


}
