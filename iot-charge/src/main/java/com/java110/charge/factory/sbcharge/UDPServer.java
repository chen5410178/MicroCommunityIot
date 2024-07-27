package com.java110.charge.factory.sbcharge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {
    public static void main(String[] args) {

        final int serverPort = 12306;
        byte[] buffer = new byte[1024];

        try (DatagramSocket socket = new DatagramSocket(serverPort)) {
            System.out.println("UDP server started on port " + serverPort);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
