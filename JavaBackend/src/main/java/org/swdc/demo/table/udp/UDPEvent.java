package org.swdc.demo.table.udp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.DatagramPacket;

/***
 * 这个是通过udp发送的消息。
 *
 * 通过继承它能得到不同类型的UDP消息，它们可以被UDPEventSender发送到
 * Electron。
 */
public class UDPEvent {

    private static ObjectMapper mapper = new ObjectMapper();

    private Object data;

    private String type;

    public UDPEvent(String type, Object data) {
        this.data = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public DatagramPacket toPacket() throws JsonProcessingException {
        byte[] data = mapper.writeValueAsBytes(this);
        return new DatagramPacket(data,0,data.length);
    }
}
