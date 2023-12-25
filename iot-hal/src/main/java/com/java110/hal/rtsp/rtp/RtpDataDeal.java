package com.java110.hal.rtsp.rtp;

import java.util.ArrayList;
import java.util.ListIterator;

public class RtpDataDeal{
    //单个文件的标志
    private boolean singleFlag = false;
    //分片时nal的头字节由FU indicator的前三位和FU Header的后五位组成
    private byte dataHead;
    private final byte[] pierce = new byte[]{0, 0, 0, 1};//固定分割0001
    private final byte[] pierceAndHead=new byte[]{0,0,0,1,-1};//固定分割0001+两个字节组合成的头
    private ArrayList<byte[]> arrayList=new ArrayList<>(128);
    private int pierceLength=0;
    //分片的起始标志
    private boolean startFlag;
    private boolean endFlag;
    //rtp头部长度,也是除开rtp头后第一个字节的下标
    private static final int rtpHeadLength=12;
    /**
     * 解包时，取FU indicator的前三位和FU Header的后五位构成一个字节
     * 功能：分析处理rtp中h.264的的indicator和header
     *
     * @param fu1 头部的后两个字节(1)
     * @param fu2 头部的后两个字节(2)
     */
    public void dealNalData(byte fu1, byte fu2) {
        //FU indicator
        //f 0表示正常，128表示错误
        int f = fu1 & 0x80;//128   0
        //NRI 重要级别，11表示非常重要
        int nri = fu1 & 0x60;
        //FU Type 表示该NALU的类型是什么 28表示FU-A分片单元 1代表不分区 7代表SPS 8代表PPS 5代表IDR
        int fu_type = fu1 & 0x1f;//为28代表分包
        //FU Header
        //起始帧，为1表示分片的第一包
        //分包的起始包
        startFlag = (128 == (fu2 & 0x80));
        //末尾帧，为1表示分片的最后一包
        //结束包
        endFlag = (64 == (fu2 & 0x40));//前面表示要分片
        //表示不分片，一次单个NAL单元
        singleFlag = fu_type <= 23 && fu_type >= 1;
        //nal类型，表示为 什么帧
        int nalType = fu2 & 0x1f;
        if (fu_type == 28) {//分包时需将FU indicator的前三位和FU Header的后五位为1个字节放入
            dataHead = (byte) (f + nri + nalType);
        }
    }


    /**
     * 功能：拼接头部的4个字节 0 0 0 1 以及 nal 和 本次的荷载数据
     * @param rtpBody 本次的数据数组去掉rtp头之后的数据
     *  返还拼接了0 0 0 1 、 nal 和 荷载数据的数组
     */
    public byte[] convergeBytes(byte[] rtpBody,int length){
        dealNalData(rtpBody[rtpHeadLength],rtpBody[rtpHeadLength+1]);//处理此次的头两字节信息
        if (singleFlag) {//不分片
            //在头部加上0 0 0 1四个字节
            byte[] newByte=new byte[4+length-12];
            System.arraycopy(pierce,0,newByte,0,4);
            System.arraycopy(rtpBody,12,newByte,4,length-12);
            return newByte;
        }else if (startFlag){//要分片,分片的第一片才加，其余不加
            //+4 是加 0 0 0 1四个字节 +1 是加合并的头  -2 是减去头部的两个字节（因为这两个要合并成一个） +4 +1 -2
            byte[] newByte=new byte[3+length-12];
            pierceAndHead[4]=dataHead;
            System.arraycopy(pierceAndHead,0,newByte,0,5);
            //在头部加上0 0 0 1四个字节 和 dataHead 取indicator前3 和 head后5
            System.arraycopy(rtpBody,14,newByte,5,length-14);
            arrayList.add(newByte);
            pierceLength+=newByte.length;
            //这里因为rtpBody是整个数据包括前两个需要合并的字节，所以需要从rtpBody的第三个下标也就是2开始复制
            //因为从第三个字节开始复制，长度也需要减去2，因为不减长度的话没这么多位
            return null;
        }else {
            byte[] newByte=new byte[length-12-2];
            System.arraycopy(rtpBody,14,newByte,0,length-14);
            arrayList.add(newByte);
            pierceLength+=newByte.length;
            if (endFlag){
                return pinJie();
            }else return null;
        }
    }
    private byte[] pinJie(){
        byte[] needSend=new byte[pierceLength];
        ListIterator<byte[]> listIterator = arrayList.listIterator();
        int index=0;
        while (listIterator.hasNext()){
            byte[] next = listIterator.next();
            System.arraycopy(next,0,needSend,index,next.length);
            index+= next.length;
        }
        arrayList=new ArrayList<>(128);
        pierceLength=0;
        return needSend;
    }
}