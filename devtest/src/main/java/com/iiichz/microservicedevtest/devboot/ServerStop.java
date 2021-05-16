package com.iiichz.microservicedevtest.devboot;

public class ServerStop {

    public void stopServer() {
        MonitorThread.stop(8081);
    }

}

