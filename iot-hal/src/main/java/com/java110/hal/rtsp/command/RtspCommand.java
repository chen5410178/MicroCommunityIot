package com.java110.hal.rtsp.command;

import com.java110.core.utils.StringUtil;
import com.java110.hal.rtsp.holder.RtspHolder;

public class RtspCommand {


    private static final String VERSION = " RTSP/1.0";
    private static String transport = "RTP/AVP/TCP;unicast;interleaved=0-1";


    private static int seq = 2;


    /**
     * 发送options
     * @param address
     */
    public static void sendOptions(String url,String address){
        StringBuilder sb = new StringBuilder();
        sb.append("OPTIONS ");
        //sb.append(address.substring(0, address.lastIndexOf("/")));
        sb.append(address);
        sb.append(VERSION);
        sb.append(System.lineSeparator());

        sb.append("Cseq: ");
        sb.append(seq++);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        System.out.println(sb.toString());
        RtspHolder.send(url,sb.toString().getBytes());
    }


    /**
     * 发送 describe
     * @param url
     * @param address
     */
    public static void sendDescribe(String url,String address,String authenticate) {
        StringBuilder sb = new StringBuilder();
        sb.append("DESCRIBE ").append(address).append(VERSION).append(System.lineSeparator());
        sb.append("Cseq: ").append(seq++).append(System.lineSeparator());
        if(!StringUtil.isEmpty(authenticate)){
            sb.append(authenticate).append(System.lineSeparator());
        }

        sb.append("Accept: application/sdp").append(System.lineSeparator());


        sb.append(System.lineSeparator());
        System.out.println(sb.toString());
        RtspHolder.send(url,sb.toString().getBytes());
    }

    /**
     * 发送setup
     * @param url
     * @param address
     * @param trackInfo
     */
    public static void doSetup(String url,String address,String trackInfo,String authenticate) {
        StringBuilder sb = new StringBuilder();
        sb.append("SETUP ");
        sb.append(address);
        sb.append("/");
        sb.append(trackInfo);
        sb.append(VERSION);
        sb.append(System.lineSeparator());

        sb.append("Cseq: ");
        sb.append(seq++);
        sb.append(System.lineSeparator());
        sb.append(authenticate).append(System.lineSeparator());
        sb.append("Transport: ");
        sb.append(transport);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        System.out.println(sb.toString());
        RtspHolder.send(url,sb.toString().getBytes());
    }

    public static void doPlay(String url,String address,String sessionid,String authenticate) {
        StringBuilder sb = new StringBuilder();
        sb.append("PLAY ");
        sb.append(address);
        sb.append(VERSION);
        sb.append(System.lineSeparator());
        sb.append("Session: ");
        sb.append(sessionid);
        sb.append(System.lineSeparator());
        sb.append("Cseq: ");
        sb.append(seq++);
        sb.append(System.lineSeparator());
        sb.append(authenticate).append(System.lineSeparator());
        sb.append("Range: npt=0.000-");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        System.out.println(sb.toString());
        RtspHolder.send(url,sb.toString().getBytes());
    }

    public static void doPause(String url,String address,String sessionid) {
        StringBuilder sb = new StringBuilder();
        sb.append("PAUSE ");
        sb.append(address);
        sb.append("/");
        sb.append(VERSION);
        sb.append(System.lineSeparator());

        sb.append("Cseq: ");
        sb.append(seq++);
        sb.append(System.lineSeparator());
        sb.append("Session: ");
        sb.append(sessionid);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        RtspHolder.send(url,sb.toString().getBytes());
        System.out.println(sb.toString());
    }

    public static  void doTeardown(String url,String address,String sessionid) {
        StringBuilder sb = new StringBuilder();
        sb.append("TEARDOWN ");
        sb.append(address);
        sb.append("/");
        sb.append(VERSION);
        sb.append(System.lineSeparator());
        sb.append("Cseq: ");
        sb.append(seq++);
        sb.append(System.lineSeparator());
        sb.append("User-Agent: RealMedia Player HelixDNAClient/10.0.0.11279 (win32)");
        sb.append(System.lineSeparator());
        sb.append("Session: ");
        sb.append(sessionid);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        RtspHolder.send(url,sb.toString().getBytes());
        System.out.println(sb.toString());
    }
}
