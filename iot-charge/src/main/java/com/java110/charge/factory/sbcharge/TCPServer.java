package com.java110.charge.factory.sbcharge;

import java.io.*;
import java.net.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TCPServer {

    public static Map<String, String> decode(String input) {
        int j = 0;
        int index = 0;
        Map<String, String> decodedMap = new LinkedHashMap<>();
        decodedMap.put("key" + j, input.substring(index, index + 8));//帧头，特殊处理下
        index += 8;
        j++;
        while (index < input.length() - 4) {
            if (j == 3) {//数据层键，特殊处理下
                decodedMap.put("key" + j, input.substring(index, index + 6));
                index += 6;
                j++;
            }
            String key = input.substring(index, index + 2);
            index += 2;
            int valueLength = Integer.parseInt(key, 16) * 2;
            String value = input.substring(index, index + valueLength);
            index += valueLength;
            decodedMap.put("key" + j, key + value);
            if (j == 11) {
                j++;
                decodedMap.put("key" + j, convertHexToASCII(value));
            }
            j++;


        }
        return decodedMap;
    }


    public static String convertHexToASCII(String original) {
        StringBuilder converted = new StringBuilder();
        if (original.length() % 2 == 0) {
            for (int i = 0; i < original.length(); i += 2) {
                String hex = original.substring(i, i + 2);
                int decimal = Integer.parseInt(hex, 16);
                char ascii = (char) decimal;
                if (Character.isLetterOrDigit(ascii)) {
                    converted.append(ascii);
                }
            }
        }
        return converted.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main(String[] args) {

        String nere = "27:0100";
        String nere1 = "28:001C0000";
       // nere = nere.substring(3);
        nere1 = nere1.substring(3);
        System.out.println("长度222: " + nere.substring(1,2));
        System.out.println("长度: " + nere1);
        System.out.println("长度: " + nere1.charAt(2));
        System.out.println("长度: " +Integer.toHexString(999));

        int decimal = 255;
        String hex = String.format("%04x", decimal);
        System.out.println(hex); // 输出FFFF

//        encodedString = encodedString.substring(8,encodedString.length());
//        encodedString = encodedString.substring(0,encodedString.length()-5);


        String encodedString = "070101010000A00007010262061000030F010303010405040105000003013001";

        splitIntoPairs(encodedString,4);
        //注册测试
        UnpackBKVResult result = BKV.unpack(CodecUtil.hexToBytes(encodedString));
//        System.out.println("长度: " + kv.getKey());
//        System.out.println("长度: " + CodecUtil.bytesToHex(kv.getValue()));
        BKV bkv = result.getBKV();
        // System.out.println("长度: " + bkv.getItems());
        if (bkv.getItems().size() == 3) {
            //UnpackBKVResult result2 = BKV.unpack(CodecUtil.hexToBytes(CodecUtil.bytesToHex(bkv.getItems().get(2).getValue())));
            UnpackBKVResult result2 = BKV.unpack(CodecUtil.hexToBytes(CodecUtil.bytesToHex(bkv.getItems().get(2).getValue())));
            BKV bkv2 = result2.getBKV();
            for (KV kv1 : bkv2.getItems()) {
                bkv.getItems().add(kv1);
            }
        }
        int i = 1;
        for (KV kv1 : bkv.getItems()) {
            System.out.println("key" + i + ":" + String.format("%02X", Integer.parseInt(Integer.toHexString(kv1.getNumberKey().intValue()), 16)) + ":" + CodecUtil.bytesToHex(kv1.getValue()));
            i++;
        }
        //        try {
//            // 打印解码后的键值对
//            for (Map.Entry<String, String> entry : decodedMap.entrySet()) {
//
//            }
//        } catch (StringIndexOutOfBoundsException e) {
//            System.out.println("解码失败：输入字符串格式不正确");
//        }


        //   startServer();
//
//        String freeWaitTime = String.format("%X", 15);
        System.out.println("03013D00".substring(5, 6));
//        System.out.println("03013D13".substring(6,8));
//        String paramOut = String.format("%04d", 256);
//        System.out.println(paramOut);
        //convertHexToASCII("01143839383630343938313032324330343038353530");

        // String encodedString = "68005768070101010000A000070102620610000346010303010402040110B8DA0601110880574506011201000300060113010000000301160006011A0006050316011438393836303439383130323243303430383535300301153C";

        //  System.out.println(encodedString.substring(30,40));

        //设备状态应答
        //  String encodedString = "6800XX68070101010000000007010262061000030A01030301040C03012C01";
//        68003F68(帧头) 070101010000A000(控制层) 0701026206100003(地址层)
//                2E0103030104070C01270000000000000000000016012800000000000000000000000000000000000
//        000000301153C(数据层) 38(校验) 16(帧尾)
//
//                2E0103(数据层键) 03010407(功能码:07状态上报) 0C012700000000000000000000(通道开关
//:27) 1601280000000000000000000000000000000000000000(通道功率:28) 0301153C(信号值
//:3C)
//
////收到心跳
//        68002B68
//        070101010000A000
//        0701028121900615
//        1A0103
//    03010407
//    04012700000601


//
//    28000000000301154B03011D5A   --6816


        //启动空载时间设置
        // String encodedString = "68001F68070101010000000007010262061000030E0103070205DC0000000C03010404";

        //设置语音广告
        //  String encodedString = "6800266807010101000000000701026206100001150103030104040E020500C4E3BAC3CCE1D0D1C4FA2C";

        //设置语音广告
//
//        68002b68070101010000a00007010281219006151a0103030104070401270100060128000000000301153903011d595616
//        68002b68070101010000a00007010281219006151a0103030104070401270100060128000000000301154b03011d596816
        //     String encodedString = "68002b68070101010000a00007010281219006151a0103030104070401270100060128000000000301153f03011d585b16";

////运营平台远程控制
//   String encodedString =  "680024680701010100000000070102620610000313010303010405030130010301590004015A0258";
        //远程停机回复
        //String encodedString = "68002068070101010000A00007010262061000030F0103030104050401050000030130008D16";
        // String encodedString = "68002068070101010000A00007010262061000030F010303010405040105000003018000DD16";
        //设备状态改变上报

        //重启响应
        // 68002068(帧头) 070101010000A000(控制层) 0701026206100003(地址层)
        //     0F010303010405040105000003018000(数据层) DD(校验) 16(帧尾)

        // (地址层)
        //     (数据层) (校验) (帧尾)


//        int bitGroupSum = calculateBitGroupSum(encodedString);

        //System.out.println("长度: " + encodedString.length());
        // System.out.println("长度: " + "070101010000000007010281219006150A01030301040703010500".length());
//        68002068(帧头) 070101010000A000(控制层) 0701026206100003(地址层)
//                0F010303010407030130000401061001(数据层) A1(校验) 16(帧尾
//
//        68003F68(帧头) 070101010000A000(控制层) 0701026206100003(地址层)
//                2E0103030104070C01270000000000000000000016012800000000000000000000000000000000000
//        000000301153C(数据层) 38(校验) 16(帧尾)

        //   calculateFrameHeaderLength("070101010000000007010281219006150A01030301040703010500");


        //  Map<String, String> decodedMap = decode(encodedString);
//
//
//
//
//
//
//        System.out.println(decodedMap.get("key4").substring(6, 8));
//        System.out.println(decodedMap.get("key5").substring(6,10));
//        System.out.println(decodedMap.get("key6").substring(6,8));
//        try {
//            // 打印解码后的键值对
//            for (Map.Entry<String, String> entry : decodedMap.entrySet()) {
//                System.out.println(entry.getKey() + ":" + entry.getValue());
//            }
//
//
//        } catch (StringIndexOutOfBoundsException e) {
//            System.out.println("解码失败：输入字符串格式不正确");
//        }

        //充电桩，心跳 上报通道状态，
//      encodedString = "68003F68070101010000A00007010262061000092E0103030104070C01270000000000000000000016012800000000000000000000000000000000000000000301153C";
//
//         decodedMap = decode(encodedString);
//        try {
//            // 打印解码后的键值对
//            for (Map.Entry<String, String> entry : decodedMap.entrySet()) {
//                System.out.println(entry.getKey() + ":" + entry.getValue());
//            }
//        } catch (StringIndexOutOfBoundsException e) {
//            System.out.println("解码失败：输入字符串格式不正确");
//        }

//        String dataBody = "0000000000000000000000000000000000000000";
//
//
//        System.out.println(dataBody.length());


//        int port = 12345;
//
//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            System.out.println("Server started. Waiting for client connection...");
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
//
//                // 进行客户端认证
//                if (authenticateClient(clientSocket)) {
//                    handleClientRequest(clientSocket);
//                } else {
//                    System.out.println("Client authentication failed. Closing connection.");
//                    clientSocket.close();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static String[] splitIntoPairs(String input,int len) {
        if (input == null || input.isEmpty()) {
            return new String[0];
        }
        if (input.length() % len != 0) {
            return new String[0];
        }

        String[] pairs = new String[input.length() / len];
        for (int i = 0, j = 0; i < input.length(); i += len, j++) {
            pairs[j] = input.substring(i, i + len);
        }

        for (int i = 0; i < pairs.length; i++) {
            System.out.print(pairs[i] + " ");
        }
        System.out.println();
        return pairs;
    }



    // 计算字节数组中所有字节的8位位组算术和
    private static int calculateBitGroupSum(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        int sum = 0;
        for (byte b : data) {
            int val = b & 0xFF; // 确保为无符号整数
            sum += val;
        }

        String hex = Integer.toHexString(sum).toUpperCase(); // 将十进制数转换为十六进制字符串
        String result = hex.substring(hex.length() - 2); // 获取十六进制字符串的最后两位
        System.out.println("Hexadecimal representation: 0x" + result); // 输出最终结果
        return sum;
    }


    //计算帧头部分长度
    private static String calculateFrameHeaderLength(String s) {
        int len = s.length();
        String hex = Integer.toHexString(len / 2);
        System.out.println("长度：" + hex);
        return hex;
    }

    private static void startServer2() {
        try {
            // 创建ServerSocket对象，指定监听的端口号
            ServerSocket serverSocket = new ServerSocket(12306);
            System.out.println("Server started. Waiting for client connection...");

            // 等待客户端连接
            Socket clientSocket = serverSocket.accept();
            String clientIP = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Client connected.");
            System.out.println("Client IP: " + clientIP);
            System.out.println("channel:" + clientSocket.getChannel());
            // 创建输入流和输出流
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 保持连接，随时进行数据交互
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                out.println("Server received: " + inputLine);

                if (inputLine.equals("bye")) {
                    break;
                }
            }

            // 关闭连接
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void startServer() {


        while (true) {
            try {
                // 创建ServerSocket对象，绑定端口号
                ServerSocket serverSocket = new ServerSocket(12306);

                System.out.println("Server started. Waiting for client connection...");

                // 监听客户端连接
                Socket clientSocket = serverSocket.accept();

                System.out.println("Client connected.");

                String clientIP = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Client IP: " + clientIP);
                System.out.println("channel:" + clientSocket.getChannel());
                // 创建输入流读取客户端发送的数据
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // 创建输出流向客户端发送数据
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 读取客户端发送的数据并打印
                String message = in.readLine();
                System.out.println("Received message from client: " + message);

                // 向客户端发送响应数据
                out.println("Server response: " + message);

                // 关闭连接
                in.close();
                out.close();
                clientSocket.close();
                serverSocket.close();

                System.out.println("Server stopped.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean authenticateClient(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        String username = reader.readLine();
        String password = reader.readLine();

        // 在这里进行客户端的认证逻辑
        // 比如，可以检查用户名和密码是否正确

        if (username.equals("admin") && password.equals("password")) {
            writer.println("Authentication successful");
            return true;
        } else {
            writer.println("Authentication failed");
            return false;
        }
    }

    private static void handleClientRequest(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        String clientMessage = reader.readLine();
        System.out.println("Received message from client: " + clientMessage);

        // 在这里根据客户端的请求进行处理
        // 这里只是简单地返回一个字符串
        writer.println("Hello from server");

        // 服务端不主动关闭客户端连接，等待客户端自己要求关闭连接
    }
}

