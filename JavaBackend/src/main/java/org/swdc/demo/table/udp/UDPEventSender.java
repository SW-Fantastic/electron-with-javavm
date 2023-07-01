package org.swdc.demo.table.udp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.demo.table.UDPConfig;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 向指定UDP端口发送消息的工具类
 */
public class UDPEventSender {

    private static final Logger logger = LoggerFactory.getLogger(UDPEventSender.class);

    public static void sendEvent(UDPEvent data) {

        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = data.toPacket();
            packet.setAddress(InetAddress.getLoopbackAddress());
            packet.setPort(UDPConfig.getUdpPort());
            socket.send(packet);
            socket.close();
            if (data.getType().equals("log")) {
                return;
            }
            logger.info("event : [" + data.getType() + "] has send to " + packet.getAddress().getHostName() + ":" + packet.getPort());
        } catch (Exception e) {
            logger.error("failed to send udp event.", e);
        }

    }

}
