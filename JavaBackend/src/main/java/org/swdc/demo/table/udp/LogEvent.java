package org.swdc.demo.table.udp;

/**
 * Log消息，来自Logback的Appender。
 */
public class LogEvent extends UDPEvent {
    public LogEvent(LogEventData data) {
        super("log", data);
    }

}
