package org.swdc.demo.table.udp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logback的Appender。
 *
 * 它负责将Log通过UDP发送到Electron。
 */
public class UDPLoggerAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        Level level = iLoggingEvent.getLevel();
        IThrowableProxy proxy = iLoggingEvent.getThrowableProxy();
        StringBuilder sb = new StringBuilder(iLoggingEvent.getFormattedMessage());

        if (proxy instanceof ThrowableProxy) {
            StringWriter sw = new StringWriter();
            PrintWriter printWriter = new PrintWriter(sw);
            ThrowableProxy throwableProxy = (ThrowableProxy) proxy;
            throwableProxy.getThrowable().printStackTrace(printWriter);
            sb.append("\n").append(sw);
        }
        LogEventData event = new LogEventData(level.levelStr,sb.toString());
        UDPEventSender.sendEvent(new LogEvent(event));
    }

}
