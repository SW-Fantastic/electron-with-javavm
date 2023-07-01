package org.swdc.demo.table.udp;

import java.util.Map;

/**
 * 状态消息，在SpringBoot启动后发送此消息，
 * 用以通知Electron本应用启动时使用的端口号。
 */
public class StatusEvent extends UDPEvent {

    public StatusEvent(String state, String port) {
        super("status", Map.of(
                "state", state,
                "port", port
        ));
    }
}
