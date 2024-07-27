package com.java110.charge.factory.sbcharge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) {
        try {
            // 创建Socket对象，指定服务器地址和端口号
            Socket socket = new Socket("localhost", 12345);

            // 创建输入流读取服务器发送的数据
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 创建输出流向服务器发送数据
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 向服务器发送数据
            out.println("Hello server!");

            // 读取服务器发送的响应数据并打印
            String response = in.readLine();
            System.out.println("Received response from server: " + response);

            // 关闭连接
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
