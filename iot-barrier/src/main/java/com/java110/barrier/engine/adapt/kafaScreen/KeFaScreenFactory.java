package com.java110.barrier.engine.adapt.kafaScreen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.barrier.engine.adapt.zhenshiMqtt.JinjieScreenMqttFactory;
import com.java110.barrier.engine.adapt.zhenshiMqtt.ZhenshiMqttSend;
import com.java110.core.utils.Base64Convert;
import com.java110.core.utils.DateUtil;
import com.java110.core.utils.StringUtil;
import com.java110.dto.barrier.BarrierDto;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class KeFaScreenFactory {
    private static Logger logger = LoggerFactory.getLogger(KeFaScreenFactory.class);

    /**
     * 0x00 连接 PSWD[6~32] RET + HVR[3] + SVR[3] + CPVR[3]
     * 1.0.0
     * 0x02 修改密码 PSWD[6~32] ACK(成功为 0，失败返回非 0 的值)
     * 0x05 同步时间 Y[2] + M + D + W + H + N + S ACK(成功为 0，失败返回非 0 的值)
     * 0x07 更改通信地址 NDA ACK(成功为 0，失败返回非 0 的值)
     * 0x0A 更改波特率 BAUD[4] ACK(成功为 0，失败返回非 0 的值)
     * 0x0C 调整显示亮度 LIGHT ACK(成功为 0，失败返回非 0 的值)
     * 0x0D 调整音量 VOL ACK(成功为 0，失败返回非 0 的值)
     * 0x0F 设置继电器状态 CH+OF+RESERVED[3] + OT ACK(成功为 0，失败返回非 0 的值)
     * 0x1A 设置 485 模式 MODE ACK(成功为 0，失败返回非 0 的值)
     * 0x1E 恢复出厂设置 无 ACK(成功为 0，失败返回非 0 的值)
     * 0xD3 配置临时车权限 TCA ACK(成功为 0，失败返回非 0 的值)
     * 0xD4 获取 GPS 位置信息 无 MSG
     * <p>
     * 0x30 播放语音 OPT + TEXT[MAX 254] ACK(成功为 0，失败返回非 0 的值)
     * 0x31 停止播放语音 无 ACK(成功为 0，失败返回非 0 的值)
     * 显 示 接 口
     * 0x62 下载临时信息 TWID + ETM + ETS + DM + DT + EXM + EXS + FINDEX + DRS + TC[4] + BC[4] + TL[2] + TEXT[...] ACK(成功为 0，失败返回非 0 的值)
     * 0x67 下载广告语 TWID + FID + AF + ETM + ETS + DM + DT + EXM + EXS + FINDEX + TC[4] + BC[4] + TL[2] + TEXT[...] ACK(成功为 0，失败返回非 0 的值)
     * 0x68 显示广告语 TWID + FID ACK(成功为 0，失败返回非 0 的值)
     * 0x6E 多行单包显示带语音 SAVE_FLAG + TEXT_CONTEXT_NUMBER + TEXT_CONTEXT[…]+ VF+ VTL + VT[...] ACK(成功为 0，失败返回非 0 的值)
     * 0xE1 扫码支付界面 SF + EM + ETM + ST + NI + TIME + MONEY + ML + TL + FLAGS + QRSIZE + RESERVED[15] + MSG[ML + 1] + TEXT[TL + 1] ACK(成功为 0，失败返回非 0 的值)
     * 0xE3 余位显示界面 SF + EM + ETM + ST + NI + NUM ACK(成功为 0，失败返回非 0 的值)
     * 0xE5 扫码支付界面 SF + EM + ETM + ST + NI + VEN + TL + TEX
     */

    public static final byte[] CMD_CONNECT = {0x00};
    public static final byte[] CMD_CHANGE_PWD = {0x02};
    public static final byte[] CMD_SYNC_TIME = {0x05};
    public static final byte[] CMD_CHANGE_ADDRESS = {0x07};
    public static final byte[] CMD_CHANGE_BRIGHTNESS = {0x0C};
    public static final byte[] CMD_SETTING_485 = {0x1A};
    public static final byte[] CMD_PLAY = {0x30};
    public static final byte[] CMD_STOP_PLAY = {0x31};
    public static final byte[] CMD_DOWNLOAD_TEMP_INFO = {0x62};//下载临时信息
    public static final byte[] CMD_DOWNLOAD_AD = {0x67};//下载广告语
    public static final byte[] CMD_VIEW_AD = {0x68};//下载广告语
    public static final byte[] CMD_VIEW_QRCODE = {(byte) 0xE1};//显示二维码
    public static final byte[] CMD_VIEW_PLAY = {(byte) 0x6E};//显示并播放

    public static final byte[] DA = {0x00};
    public static final byte[] VR = {0x64};
    public static final byte[] PN = {(byte) 0xFF, (byte) 0xFF};
    public static final byte[] COLOR_RED = {(byte) 0xFF, 0x00, 0x00, 0x00};
    public static final byte[] GB_COLOR = {0x00, 0x00, 0x00, 0x00};
    public static final byte[] COLOR_GREEN = {0x00, (byte) 0xFF, 0x00, 0x00};


    //语音变量

    /**
     * 0x00 添加到语音队列但是不开始播放
     * 0x01 添加到语音队列并且开始播放
     * 0x02 先清除队列，再添加新语音到队列，然后开始播放
     */

    public static final byte CMD_PLAY_ADD = 0x00;
    public static final byte CMD_PLAY_AUTO = 0x01;
    public static final byte CMD_PLAY_DELETE_ADD = 0x02;


    /**
     * 获取 语音数据
     *
     * @param msg
     * @return
     */
    public static String getPayData(String msg) {
        Date startTime = DateUtil.getCurrentDate();

        byte[] data = new byte[0];
        try {
            data = msg.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        int textLen = data.length;
        byte[] datalength = {(byte) (1 + textLen), 0x01};
        data = ArrayUtils.addAll(datalength, data);
        return getSendData(CMD_PLAY, data);

        //System.out.println("------------------------------------------------------发送语音耗时：" + (DateUtil.getCurrentDate().getTime() - startTime.getTime()));

    }

    /**
     * 获取下发文字
     *
     * @param line
     * @param msg
     * @return
     */
    public static String getDownloadTempTexts(int line, String msg) {
        return getDownloadTempTexts(line,msg,(byte) 0x10, (byte) 0x01);
    }

    /**
     * 获取下发文字
     *
     * @param line
     * @param msg
     * @return
     */
    public static String getDownloadTempTexts(int line, String msg, byte dt, byte drs) {
        Date startTime = DateUtil.getCurrentDate();
        if (StringUtil.isEmpty(msg)) {
            return "";
        }

        //0x00, 0x04
        byte[] data = new byte[]{};
        //演示 红色 绿色
        byte[] color = line % 2 == 0 ? COLOR_RED : COLOR_GREEN;
        // 设置  TWID + ETM + ETS + DM + DT + EXM + EXS + FINDEX + DRS + TC[4] + BC[4] + TL[2] + TEXT[...]
        byte[] tmpDate = new byte[]{(byte) line, 0x01, 0x01, 0x00, dt, 0x01, 0x01, 0x03, drs};
        tmpDate = ArrayUtils.addAll(tmpDate, color); //tc
        tmpDate = ArrayUtils.addAll(tmpDate, GB_COLOR); //BC
        try {
            tmpDate = ArrayUtils.addAll(tmpDate, intCovertByte(msg.getBytes("GBK").length));
            tmpDate = ArrayUtils.addAll(tmpDate, msg.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        data = ArrayUtils.addAll(data, tmpDate);
        int textLen = data.length;
        byte[] datalength = {(byte) (textLen)};
        data = ArrayUtils.addAll(datalength, data);

        return getSendData(CMD_DOWNLOAD_TEMP_INFO, data);

        //System.out.println("------------------------------------------------------发送屏显耗时：" + (DateUtil.getCurrentDate().getTime() - startTime.getTime()));
    }

    /**
     * 发送数据
     *
     * @param cmd
     * @param data
     */
    private static String getSendData(byte[] cmd, byte[] data) {
        int textLen = data.length;
        if ((1 + textLen) >= 255) { //数据最大不能超过255长度
            throw new IllegalArgumentException("数据最大不能超过255长度");
        }
        //拼接 va vr pn  cmd dl  data  crc
        byte[] newData = ArrayUtils.addAll(DA, VR);
        newData = ArrayUtils.addAll(newData, PN);
        newData = ArrayUtils.addAll(newData, cmd);
        newData = ArrayUtils.addAll(newData, data);
        byte[] crc = getCrc(newData);
        newData = ArrayUtils.addAll(newData, crc);
        System.out.println("语音数据" + bytes2hex03(newData));

        return Base64Convert.byteTobase64(newData);
    }


    /**
     * 计算crc 16
     *
     * @param data
     * @return
     */
    private static byte[] getCrc(byte[] data) {
        byte[] crc16_h = {
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
                (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40
        };

        byte[] crc16_l = {
                (byte) 0x00, (byte) 0xC0, (byte) 0xC1, (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2, (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05, (byte) 0xC5, (byte) 0xC4, (byte) 0x04,
                (byte) 0xCC, (byte) 0x0C, (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE, (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B, (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8,
                (byte) 0xD8, (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB, (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF, (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC,
                (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7, (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12, (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0, (byte) 0x10,
                (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1, (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36, (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35, (byte) 0x34, (byte) 0xF4,
                (byte) 0x3C, (byte) 0xFC, (byte) 0xFD, (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE, (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39, (byte) 0xF9, (byte) 0xF8, (byte) 0x38,
                (byte) 0x28, (byte) 0xE8, (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A, (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF, (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C,
                (byte) 0xE4, (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7, (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3, (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0,
                (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63, (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6, (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64, (byte) 0xA4,
                (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D, (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA, (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9, (byte) 0xA8, (byte) 0x68,
                (byte) 0x78, (byte) 0xB8, (byte) 0xB9, (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA, (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D, (byte) 0xBD, (byte) 0xBC, (byte) 0x7C,
                (byte) 0xB4, (byte) 0x74, (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6, (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73, (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0,
                (byte) 0x50, (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53, (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57, (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54,
                (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F, (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A, (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58, (byte) 0x98,
                (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89, (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E, (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D, (byte) 0x4C, (byte) 0x8C,
                (byte) 0x44, (byte) 0x84, (byte) 0x85, (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86, (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41, (byte) 0x81, (byte) 0x80, (byte) 0x40
        };

        int crc = 0x0000ffff;
        int ucCRCHi = 0x00ff;
        int ucCRCLo = 0x00ff;
        int iIndex;
        for (int i = 0; i < data.length; ++i) {
            iIndex = (ucCRCLo ^ data[i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_h[iIndex];
            ucCRCHi = crc16_l[iIndex];
        }

        crc = ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
        //高低位互换，输出符合相关工具对Modbus CRC16的运算
        crc = ((crc & 0xFF00) >> 8) | ((crc & 0x00FF) << 8);
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((crc >> 8) & 0xff);
        bytes[1] = (byte) (crc & 0xff);
        return bytes;
    }

    public static String bytes2hex03(byte[] bytes) {
        final String HEX = "0123456789ABCDEF";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0F));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0F));
            sb.append(" ");
        }

        return sb.toString();
    }


    /**
     * int类型转换成byte数组
     *
     * @param param int int类型的参数
     */

    public static byte[] intCovertByte(int param) {
        byte[] arr = new byte[2];
        arr[0] = (byte) ((param >> 8) & 0xff);
        arr[1] = (byte) (param & 0xff);
        return arr;
    }


}
