package com.java110.hal.nettty;

import com.java110.core.utils.BytesUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class CustomDecoder  extends MessageToMessageDecoder<byte[]> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);


    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {

        LOGGER.debug("-------------解码器接收到数据：{}",BytesUtil.bytesToHex(msg));
        try {
            out.add(msg);
           // if (IcojooUtil.isYunKuaiChong(msg)) {
                // 云快充
            //    unpackSH(msg, out);
           // }else {
            //    LOGGER.info( " unkown proctrol 未知协议类型" );
                //out.add(msg);
           // }

        } catch (Exception e) {
            LOGGER.error(" decode Exception : " + e.toString());
        }
    }


    /**
     * 云快充拆包
     */
    private void unpackSH(byte[] msg, List<Object> out) {
        // 获取报文的长度帧，并转化10进制字节，在加上起始帧1个字节+长度帧1个字节+效验帧2个字节，所以+4，
        int len = BytesUtil.toInt2(msg, 1)+4;
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
