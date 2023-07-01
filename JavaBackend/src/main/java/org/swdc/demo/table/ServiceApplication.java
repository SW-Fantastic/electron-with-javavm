package org.swdc.demo.table;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.swdc.demo.table.udp.StatusEvent;
import org.swdc.demo.table.udp.UDPEventSender;

@SpringBootApplication
public class ServiceApplication {


	public static void main(String[] args) {
		// 启动SpringBoot将会阻塞线程，因此在新的Thread里面做这件事情。
		new Thread(() -> {
			// 接受来自Electron传入的UDP端口，用以实现UDP的通讯功能
			int udpPort = Integer.parseInt(args[0]);
			UDPConfig.setUdpPort(udpPort);
			// 启动SpringBoot
			ConfigurableApplicationContext ctx = SpringApplication.run(ServiceApplication.class);
			// 读取并且发送Http运行的端口号。
			UDPEventSender.sendEvent(new StatusEvent(
					"ready", ctx.getEnvironment().getProperty("local.server.port")
			));
		}).start();
	}

}
