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

public class CustomDecoder extends MessageToMessageDecoder<byte[]> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);

    private Map<String, DataByteDto> dataByteMap;

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        LOGGER.debug("-------------解码器接收到数据：{}", msg);
        LOGGER.debug("-------------解码器接收到数据16：{}", BytesUtil.bytesToHex(msg));

        try {

            Channel channel = ctx.channel();
            InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
            String ip = address.toString();

            if (dataByteMap == null) {
                dataByteMap = new HashMap<>();
            }

            LOGGER.debug("channelActive --> RemoteAddress : " + ip + " channelRead ");

            // 计算完整包的长度
            int totalLen;
            if (DataHeader.isYunKuaiChongCharge(msg)) {
                totalLen = DataHeader.getYKCLength(msg) + 4;
            } else if (DataHeader.isLvChongChong(msg)) {
                totalLen = DataHeader.getLvChongChongLength(msg) + 6;
            } else if (DataHeader.isSbCharge(msg)) {
                totalLen = DataHeader.getLvChongChongLength(msg) + 6;
            } else {
                // 如果不是已知协议，直接返回
                out.add(msg);
                return;
            }

            // 如果当前数据已经是完整包，直接返回
            if (msg.length >= totalLen) {
                out.add(msg);
                return;
            }

            // 处理分包
            DataByteDto dataByteDto = dataByteMap.get(ip);
            if (dataByteDto == null) {
                // 初始化缓存
                byte[] data = new byte[1024 * 1024]; // 1MB 缓存
                dataByteDto = new DataByteDto(ip, data, 0, totalLen);
                dataByteMap.put(ip, dataByteDto);
            }

            // 检查缓存是否足够
            byte[] data = dataByteDto.getData();
            int dataLen = dataByteDto.getDataLen();
            if (dataLen + msg.length > data.length) {
                // 缓存不足，扩容
                byte[] newData = new byte[data.length * 2]; // 扩容为原来的两倍
                System.arraycopy(data, 0, newData, 0, dataLen);
                data = newData;
                dataByteDto.setData(data);
            }

            // 将新数据拼接到缓存中
            System.arraycopy(msg, 0, data, dataLen, msg.length);
            dataLen += msg.length;
            dataByteDto.setDataLen(dataLen);

            // 如果数据长度达到预期，处理完整包
            if(dataLen<totalLen){
                return;
            }
            byte[] dataMsg = new byte[dataByteDto.getTotalLen()];
            System.arraycopy(data, 0, dataMsg, 0, dataLen);
            out.add(dataMsg);
            dataByteMap.remove(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 云快充拆包
     */
    private void unpackSH(byte[] msg, List<Object> out) {
        // 获取报文的长度帧，并转化10进制字节，在加上起始帧1个字节+长度帧1个字节+效验帧2个字节，所以+4，
        int len = DataHeader.getLvChongChongLength(msg) + 6;
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
                + ctx.channel().toString());
        ctx.close();
    }

}