package com.iiichz.microservicedevtest.devboot;

import com.iiichz.devboot.DevBoot;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStart {

    private static final Logger logger = LoggerFactory.getLogger(ServerStart.class);

    private MonitorThread monitor;
    private int port = 0;

    public void start(String[] args, boolean wait) throws Exception {
        if (wait) {
            try {
                logger.info("attempting to stop server if it is already running");
                new ServerStop().stopServer();
            } catch (Exception e) {
                logger.info("failed to stop server (was probably not up): {}", e.getMessage());
            }
        }

        Tomcat tomcat = new DevBoot().run();

        port = 8080;
        logger.info("started server on port: {}", port);
        if (wait) {
            int stopPort = port + 1;
            logger.info("will use stop port as {}", stopPort);
            monitor = new MonitorThread(stopPort, () -> tomcat.destroy());
            monitor.start();
            monitor.join();
        }
    }

    public int getPort() {
        return port;
    }

    public void startServer() throws Exception {
        start(new String[]{}, true);
    }

}