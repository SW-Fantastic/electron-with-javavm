package org.swdc.demo.table.udp;

/**
 * Log消息的内容
 */
public class LogEventData {

    private String level;

    private String text;

    public LogEventData(String level, String formatted) {
        this.level = level;
        this.text = formatted;
    }

    public String getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setText(String text) {
        this.text = text;
    }
}
