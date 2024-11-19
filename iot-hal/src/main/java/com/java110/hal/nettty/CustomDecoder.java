package com.java110.hal.nettty;

import com.java110.core.utils.BytesUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDecoder  extends MessageToMessageDecoder<byte[]> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);

    private Map<String,DataByteDto> dataByteMap;

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {

        LOGGER.debug("-------------解码器接收到数据：{}",msg);
        LOGGER.debug("-------------解码器接收到数据16：{}",BytesUtil.bytesToHex(msg));

        Channel channel = ctx.channel();
        InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
        String ip = address.toString();

        if(dataByteMap == null){
            dataByteMap = new HashMap<>();
        }

        LOGGER.debug("channelActive -->  RamoteAddress : " + ip + " channelRead ");

        int totalLen = msg.length;
        //todo 云快充
        if(DataHeader.isYunKuaiChongCharge(msg)){
            totalLen = DataHeader.getYKCLength(msg) + 4;
        }else if(DataHeader.isLvChongChong(msg)){
            totalLen = DataHeader.getLvChongChongLength(msg)+6;
        }else if(DataHeader.isSbCharge(msg)){
            totalLen = DataHeader.getLvChongChongLength(msg)+6;
        }
        //todo 说明是完整包
        if(msg.length >= totalLen){
            out.add(msg);
            return;
        }
        DataByteDto dataByteDto = null;
        if(!dataByteMap.containsKey(ip)){
            byte[] data = new byte[1024*1024];
            dataByteDto = new DataByteDto(ip,data,0,totalLen);
            dataByteMap.put(ip,dataByteDto);
        }else{
            dataByteDto = dataByteMap.get(ip);
        }

        byte[] data = dataByteDto.getData();
        int dataLen = dataByteDto.getDataLen();
        System.arraycopy(msg, 0, data, dataLen, msg.length);
        dataLen+=msg.length;
        dataByteDto.setData(data);
        dataByteDto.setDataLen(dataLen);

        if(dataLen<totalLen){
            return;
        }
        byte[] dataMsg = new byte[dataByteDto.getTotalLen()];
        System.arraycopy(data, 0, dataMsg, 0, dataLen);
        out.add(dataMsg);
        dataByteMap.remove(ip);
    }


    /**
     * 云快充拆包
     */
    private void unpackSH(byte[] msg, List<Object> out) {
        // 获取报文的长度帧，并转化10进制字节，在加上起始帧1个字节+长度帧1个字节+效验帧2个字节，所以+4，
        int len = DataHeader.getLvChongChongLength(msg)+6;
        if (msg.length <= len) { // 一个整包
            out.add(msg);

        } else {
            // 1.取出一个包的数据
            byte[] array = new byte[len];
            System.arraycopy(msg, 0, array, 0, len);
            out.add(array);

            // 2. 多余的数据继续分包
            int other = msg.length - len;
            array = new byte[other];
            System.arraycopy(msg, len, array, 0, array.length);
            unpackSH(array, out);

        }

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.toString();

        LOGGER.info("channelActive -->  RamoteAddress : " + ip + " connected ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(" exceptionCaught : " + cause.toString() + " ctx = "
                + ctx.channel().toString() );
        ctx.close();
    }

}