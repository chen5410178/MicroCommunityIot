package com.java110.hal.rtsp.handler;

import com.java110.core.factory.AuthenticationFactory;
import com.java110.hal.rtsp.command.RtspCommand;
import com.java110.hal.websocket.VideoStreamWebsocket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class RtspHandler extends ChannelInboundHandlerAdapter {

    private String url ;

    private String address;

    private String action;

    private String trackInfo;

    private String sessionid;

    private String username;

    private String password;

    private String realm;

    private String nonce;

    private String machineId;

    private String authenticate;

    public RtspHandler() {
    }

    public RtspHandler(String url, String address,String username,String password,String machineId) {
        this.url = url;
        this.address = address;
        this.username = username;
        this.password = password;
        this.machineId = machineId;
        this.action = "OPTIONS";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            byte[] byteBuffer = (byte[]) msg;
            byteRead(ctx,byteBuffer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Server response: RTSP/1.0 401 Unauthorized
     * WWW-Authenticate: Digest realm="IP Camera(C6503)", nonce="c25e26a483d84f21c4da8f82fb997f8e", stale="FALSE"
     * Date:  Sat, Dec 23 2023 01:30:27 GMT
     * @param ctx
     * @param bytes
     */
    private void byteRead(ChannelHandlerContext ctx,byte[] bytes){
        String tmp = "";
        // todo 未认证
        if("DESCRIBE".equals(this.action) && (new String(bytes)).startsWith("RTSP/1.0 401 Unauthorized")){
            System.out.println("Server response: " + new String(bytes));

            String paramOut = new String(bytes);
            paramOut = paramOut.substring(paramOut.indexOf("realm=")+6);

            realm = paramOut.substring(0,paramOut.indexOf(",")).replaceAll("\"","");
            paramOut = paramOut.substring(paramOut.indexOf("nonce=")+6);
            nonce =  paramOut.substring(0,paramOut.indexOf(",")).replaceAll("\"","");
            System.out.println("realm="+realm+";nonce="+nonce);

            RtspCommand.sendDescribe(url,address,getAuthenticate("DESCRIBE"));
            return;
        }
        switch (this.action){
            case "OPTIONS":
                System.out.println("Server response: " + new String(bytes));
                this.action = "DESCRIBE";
                RtspCommand.sendDescribe(url,address,"");
                break;
            case "DESCRIBE":
                System.out.println("Server response: " + new String(bytes));
                tmp = new String(bytes);
                String temp = tmp.substring(tmp.indexOf("trackID"));
                this.trackInfo = temp.split("\r\n")[0];
                this.action = "SETUP";
                RtspCommand.doSetup(url,address,this.trackInfo,getAuthenticate("SETUP"));
                break;

            case "SETUP":
                System.out.println("Server response: " + new String(bytes));
                tmp = new String(bytes);
                String tempSessionId = tmp.substring(tmp.indexOf("Session: ") + 9);
                //sessionid = tempSessionId.split("\r\n")[0];
                this.sessionid = tempSessionId.split(";")[0];
                this.sessionid = sessionid.trim();
                this.action = "PLAY";
                RtspCommand.doPlay(url,address,this.sessionid,getAuthenticate("PLAY"));
                break;
            case "PLAY":
                System.out.println("Server response: " + new String(bytes));
                this.action = "PUSH";
                break;
            case "PUSH":
                pushVideo(ctx,bytes);
                break;
        }

    }

    private void pushVideo(ChannelHandlerContext ctx, byte[] bytes) {

        System.out.println("摄像头视频流"+bytes);


        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        VideoStreamWebsocket.sendInfo(byteBuffer,this.machineId);
    }

    private String getAuthenticate(String method){
        String HA1 = AuthenticationFactory.md5(username+":"+realm+":"+password);
        String HA2 = AuthenticationFactory.md5(method+":"+address);
        String response = AuthenticationFactory.md5(HA1+":"+nonce+":"+HA2);
        this.authenticate = "Authorization: Digest username=\""+username+"\", realm=\""+realm+"\", nonce=\""+nonce+"\", uri=\""+address+"\", response=\""+response+"\"";
        return authenticate;
    }
}
