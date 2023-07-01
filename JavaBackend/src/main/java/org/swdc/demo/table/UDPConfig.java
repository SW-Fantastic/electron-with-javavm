package org.swdc.demo.table;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UDPConfig {

    private static int udpPort = 9000;

    public static int getUdpPort() {
        return udpPort;
    }

    public static void setUdpPort(int udpPort) {
        UDPConfig.udpPort = udpPort;
    }

    @Bean
    public Integer udpPort(){
        return udpPort;
    }

}
